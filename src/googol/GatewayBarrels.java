package googol;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.concurrent.*;
import java.io.*;
import java.util.*;

public class GatewayBarrels extends UnicastRemoteObject implements InterfaceGatewayBarrels {
		private Map<BarrelAM, Integer> barrelsAM;
		private Map<BarrelNZ, Integer> barrelsNZ;
		private Set<PairRespostaPalavra> buffer;

		public GatewayBarrels() throws RemoteException{
				super();
				barrelsAM = Collections.synchronizedMap(new HashMap<BarrelAM, Integer>());
				barrelsNZ = Collections.synchronizedMap(new HashMap<BarrelNZ, Integer>());
				buffer = Collections.synchronizedSet(new HashSet<PairRespostaPalavra>());
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

		public void subscribe(BarrelAM barrel){
				barrelsAM.replace(barrel, 1);
		}

		public void subscribe(BarrelNZ barrel){
				barrelsNZ.replace(barrel, 1);
		}

		public void acknoledge(BarrelAM barrel, int clock) throws RemoteException{
				if (barrelsAM.containsKey(barrel)){
						barrelsAM.replace(barrel, clock);
				}
		}
		public void acknoledge(BarrelNZ barrel, int clock) throws RemoteException{
				if (barrelsNZ.containsKey(barrel)){
						barrelsNZ.replace(barrel, clock);
				}
		}

		public void queryResponse(ArrayList<String> top10, BarrelAM barrel, int clock) throws RemoteException{
				if (barrelsAM.containsKey(barrel)){
						barrelsAM.replace(barrel, clock);
				}
				buffer.add(new PairRespostaPalavra(top10, clock));
		}

		public void queryResponse(ArrayList<String> top10, BarrelNZ barrel, int clock) throws RemoteException{
				if (barrelsNZ.containsKey(barrel)){
						barrelsNZ.replace(barrel, clock);
				}
				buffer.add(new PairRespostaPalavra(top10, clock));
		}

		public void syncResponse(Map<String, Set<String>> indRec, Map<String, Set<String>> parents, BarrelAM barrel, int clock) throws RemoteException{
				if (barrelsAM.containsKey(barrel)){
						barrelsAM.replace(barrel, clock);
				}
		}

		public void syncResponse(Map<String, Set<String>> indRec, Map<String, Set<String>> parents, BarrelNZ barrel, int clock) throws RemoteException{
				if (barrelsNZ.containsKey(barrel)){
						barrelsNZ.replace(barrel, clock);
				}
		}
}
