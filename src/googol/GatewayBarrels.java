package googol;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.concurrent.*;
import java.util.*;

/**
 * Classe que implementa um Gateway que gere apenas as funcionalidade dos Barrels
 * @author Joao Nuno Coelho
 * @version 1.0
*/
public class GatewayBarrels extends UnicastRemoteObject implements InterfaceGatewayBarrels {
		private boolean nextAM;
		private Map<Barrel, PairClockFlag> barrels;
		private Set<PairBarrelClock> bufferACK;
		private Map<PairBarrelClock, ArrayList<String>> bufferPals;
		private Map<PairBarrelClock, PairIndRecParents> bufferSync;
		private Set<PairBarrelClock> sendBuffer;

		/**
		 * Construtor do Gateway, que inicializa todas as variáveis do objeto
		 */
		GatewayBarrels() throws RemoteException{
				super();
				nextAM = true;
				barrels = new ConcurrentHashMap<Barrel, PairClockFlag>();
				bufferACK = Collections.synchronizedSet(new HashSet<PairBarrelClock>());
				bufferPals = new ConcurrentHashMap<PairBarrelClock, ArrayList<String>>();
				bufferSync = new ConcurrentHashMap<PairBarrelClock, PairIndRecParents>();
				sendBuffer = Collections.synchronizedSet(new HashSet<PairBarrelClock>());
		}

    public static void main(String args[]) {
        try {
            GatewayBarrels gateway = new GatewayBarrels();
            Registry registry = LocateRegistry.createRegistry(8183);
            registry.rebind("gateway", gateway);
            System.out.println("Server ready. Waiting for client requests...");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

		synchronized public void subscribe(Barrel barrel){
				if (barrels.containsKey(barrel)) return;
				if (nextAM){
						barrels.replace(barrel, new PairClockFlag(1, 0));
				}else{
						barrels.replace(barrel, new PairClockFlag(1, 2));
				}
				nextAM = !nextAM;
		}

		public void updateAndSyncAcknowledge(Barrel barrel, long clock) throws RemoteException{
				PairBarrelClock message = new PairBarrelClock(barrel, clock);
				if (sendBuffer.contains(message)){
						sendBuffer.remove(message);
						bufferACK.add(new PairBarrelClock(barrel, clock));
						notify();
				}
		}

		public void wordAnswer(ArrayList<String> top10, Barrel barrel, long clock) throws RemoteException{
				PairBarrelClock message = new PairBarrelClock(barrel, clock);
				if (sendBuffer.contains(message)){
						sendBuffer.remove(message);
						bufferPals.put(new PairBarrelClock(barrel, clock), top10);
						notify();
				}
		}

		public void syncRequestAnswer(Map<String, Set<String>> indRec, Map<String, Set<String>> parents, Barrel barrel, long clock) throws RemoteException{
				PairBarrelClock message = new PairBarrelClock(barrel, clock);
				if (sendBuffer.contains(message)){
						sendBuffer.remove(message);
						bufferSync.put(new PairBarrelClock(barrel, clock), new PairIndRecParents(indRec, parents));
						notify();
				}
		}

		/**
		 * Método que gere o envio do pedido sobre todos os dados de um barrel e a sua consequente resposta
		 * Comunicação at-most-once
		 * @param tries Número de tentativas de comunicação até desistencia
		 * @param timeout Quanto tempo cada tentativa de comunicação pode demorar até desistir desta
		 * @param curBarrel Barrel com quem será feita a comunicação
		 * @param curClock Stamp da comunicação
		 */
		private PairIndRecParents sendSyncQuery(int tries, long timeout, Barrel curBarrel, long curClock){
				PairBarrelClock expected = new PairBarrelClock(curBarrel, curClock);
				PairIndRecParents answer = null;
				while (tries > 0){
						tries--;
						//curBarrel.syncQuery(curClock);
						sendBuffer.add(expected);
						long maxTime = System.currentTimeMillis() + timeout;
						long curTime = System.currentTimeMillis();
						while (bufferSync.get(expected) == null && maxTime - curTime > 0){
								try{
										wait(maxTime - curTime);
								}catch (InterruptedException e){
										e.printStackTrace();	
								}
								curTime = System.currentTimeMillis();
						}
						answer = bufferSync.get(expected);
						if (answer != null){
								bufferSync.remove(expected);
								break;
						}
				}
				return answer;
		}

		/**
		 * Método que gere o envio de sincronização para um barrel e a sua consequente resposta
		 * Comunicação at-most-once
		 * @param tries Número de tentativas de comunicação até desistencia
		 * @param timeout Quanto tempo cada tentativa de comunicação pode demorar até desisitir desta
		 * @param curBarrel Barrel com que será feita a comunicação
		 * @param curClock Stamp da comunicação
		 */
		private boolean sendSyncAnswer(int tries, long timeout, Barrel curBarrel, long curClock, PairIndRecParents answer){
				PairBarrelClock expected = new PairBarrelClock(curBarrel, curClock);
				while (tries > 0){
						tries--;
						//curBarrel.syncAnswer(answer, curClock);
						sendBuffer.add(expected);
						long maxTime = System.currentTimeMillis() + timeout;
						long curTime = System.currentTimeMillis();
						while (!bufferACK.contains(expected) && maxTime - curTime > 0){
								try{
										wait(maxTime - curTime);
								}catch (InterruptedException e){
										e.printStackTrace();
								}
								curTime = System.currentTimeMillis();
						}
						if (bufferACK.contains(expected)){
								bufferACK.remove(expected);
								return true;
						}
				}
				return false;
		}

		/**
		 * Método que gere toda a sincronização de todos os barrels.
		 * Se houver um barrel sincronizado, envia a sua informação a outros do mesmo tipo.
		 * Caso não haja nenhum barrel de um certo tipo sincronizado, cada barrel partilha a sua informação com todos do seu tipo
		 * Dentro deste último caso:
		 * 	- Se um barrel não conseguir partilhar a sua informação, todos os barrels do mesmo tipo continuam como dessincronizados
		 * 	- Se um barrel não conseguir receber informação, apenas este se mantém dessincronizado
		 * @param tries Número de tentaticas que cada comunicação deve fazer até desistencia
		 * @param timeout Quanto tempo cada tentativa de comunicação pode demorar até desistir desta
		 */
		public void synchronize(int tries, long timeout){
				PairIndRecParents answerAM = null, answerNZ = null;
				Map<Barrel, PairClockFlag> shallowBarrels = new HashMap<Barrel, PairClockFlag>();
				shallowBarrels.putAll(barrels);
				long curClock;
				boolean ack;
				for (Map.Entry<Barrel, PairClockFlag> pair: shallowBarrels.entrySet()){
						Barrel barrel = pair.getKey();
						PairClockFlag value = pair.getValue();
						value.clock.set(0);
						if (value.flag == 1 && answerAM == null){
								curClock = barrels.get(barrel).clock.incrementAndGet();
								answerAM = this.sendSyncQuery(tries, timeout, barrel, curClock-1);	
						}else if (value.flag == 3 && answerNZ == null){
								curClock = barrels.get(barrel).clock.incrementAndGet();
								answerNZ = this.sendSyncQuery(tries, timeout, barrel, curClock-1);	
						}
				}
				int amCount = 0, nzCount = 0;
				if (answerAM != null) amCount = -1;
				if (answerNZ != null) nzCount = -1;
				for (Map.Entry<Barrel, PairClockFlag> sourcePair: shallowBarrels.entrySet()){
						Barrel sourceBarrel = sourcePair.getKey();
						PairClockFlag value = sourcePair.getValue();
						if ((value.flag == 0 && answerAM == null) || (value.flag == 2 && answerNZ == null)){
								curClock = barrels.get(sourceBarrel).clock.incrementAndGet();
								PairIndRecParents answer = this.sendSyncQuery(tries, timeout, sourceBarrel, curClock-1);
								if (value.flag == 0) amCount++;
								else nzCount++;
								if (answer != null){
										for (Map.Entry<Barrel, PairClockFlag> destPair: shallowBarrels.entrySet()){
												Barrel destBarrel = destPair.getKey();
												if (destPair != sourcePair && destPair.getValue().flag == value.flag){
														curClock = barrels.get(destBarrel).clock.incrementAndGet();
														ack = this.sendSyncAnswer(tries, timeout, destBarrel, curClock-1, answer);
														if (!ack) System.out.println("Sync Data to Barrel failed");
														else barrels.get(destBarrel).clock.incrementAndGet();
												}
										}
								}else{
										System.out.println("Sync Query to Barrel failed");
										if (value.flag == 0) amCount = -1;
										else nzCount = -1;
										if (amCount == -1 && nzCount == -1) break;
								}
						}else if ((value.flag == 0 && answerAM != null) || (value.flag == 2 && answerNZ != null)){
								curClock = barrels.get(sourceBarrel).clock.incrementAndGet();
								if (value.flag == 0) ack = this.sendSyncAnswer(tries, timeout, sourceBarrel, curClock-1, answerAM);
								else ack = this.sendSyncAnswer(tries, timeout, sourceBarrel, curClock-1, answerNZ);
								if (!ack) System.out.println("Sync Data to Barrel failed");
								else barrels.get(sourceBarrel).flag = value.flag + 1;
						}								
				}
				if (amCount > 0 || nzCount > 0){
						for (Map.Entry<Barrel, PairClockFlag> pair: shallowBarrels.entrySet()){
								PairClockFlag value = pair.getValue();
								if (amCount > 0 && value.flag == 0 && value.clock.get() == amCount){
										barrels.get(pair.getKey()).flag = 1;
								}else if (nzCount > 0 && value.flag == 2 && value.clock.get() == nzCount){
										barrels.get(pair.getKey()).flag = 3;
								}
						}
				}
		}
}
