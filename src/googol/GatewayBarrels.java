package googol;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.concurrent.*;
import java.io.*;
import java.util.*;

public class GatewayBarrels extends UnicastRemoteObject implements InterfaceGatewayBarrels {
		private boolean nextAM;
		private Map<Barrel, PairClockFlag> barrels;
		private Set<PairBarrelClock> bufferACK;
		private Map<PairBarrelClock, ArrayList<String>> bufferPals;
		private Map<PairBarrelClock, PairIndRecParents> bufferSync;

		GatewayBarrels() throws RemoteException{
				super();
				nextAM = true;
				barrels = new ConcurrentHashMap<Barrel, PairClockFlag>();
				bufferACK = Collections.synchronizedSet(new HashSet<PairBarrelClock>());
				bufferPals = new ConcurrentHashMap<PairBarrelClock, ArrayList<String>>();
				bufferSync = new ConcurrentHashMap<PairBarrelClock, PairIndRecParents>();
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

		public void acknowledgeUpdate(Barrel barrel, int clock) throws RemoteException{
				bufferACK.add(new PairBarrelClock(barrel, clock));
				notify();
		}

		public void queryResponse(ArrayList<String> top10, Barrel barrel, int clock) throws RemoteException{
				bufferPals.put(new PairBarrelClock(barrel, clock), top10);
				notify();
		}

		public void syncResponse(Map<String, Set<String>> indRec, Map<String, Set<String>> parents, Barrel barrel, int clock) throws RemoteException{
				bufferSync.put(new PairBarrelClock(barrel, clock), new PairIndRecParents(indRec, parents));
				notify();
		}

		private PairIndRecParents sendSyncQuery(int tries, long timeout, Barrel curBarrel, int curClock){
				PairBarrelClock expected = new PairBarrelClock(curBarrel, curClock);
				PairIndRecParents answer = null;
				while (tries > 0){
						tries--;
						//curBarrel.syncQuery(curClock);
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
						if (answer != null) break;
				}
				return answer;
		}

		public void synchronize(int tries, long timeout){
				Barrel barrelAMSynched = null, barrelNZSynched = null;
				boolean stopFlag = false;
				for (Map.Entry<Barrel, PairClockFlag> barrel: barrels.entrySet()){
						if (barrel.getValue().flag == 1){
								barrelAMSynched = barrel.getKey();
								if (stopFlag) break;
								stopFlag = true;
						}else if (barrel.getValue().flag == 3){
								barrelNZSynched = barrel.getKey();
								if (stopFlag) break;
								stopFlag = true;
						}
				}
				if (barrelAMSynched == null && barrelNZSynched == null){
						Map<Barrel, PairClockFlag> shallowBarrels = new HashMap<Barrel, PairClockFlag>();
						shallowBarrels.putAll(barrels);
						for (Map.Entry<Barrel, PairClockFlag> sourceBarrel: shallowBarrels.entrySet()){
								boolean isAM;
								Barrel curBarrel = sourceBarrel.getKey();
								PairClockFlag value = sourceBarrel.getValue();
								int curFlag = value.flag;
								if (curFlag < 2) isAM = true;
								else isAM = false;
								int curClock = barrels.get(value).clock.incrementAndGet();
								PairIndRecParents answer = this.sendSyncQuery(tries, timeout, curBarrel, curClock);	
								if (answer == null){
										System.out.println("Sync Query to Barrel failed");
								}else{
										
						}				
				}
		}
}
