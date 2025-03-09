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
				barrels = Collections.synchronizedMap(new HashMap<Barrel, PairClockFlag>());
				bufferACK = Collections.synchronizedSet(new HashSet<PairBarrelClock>());
				bufferPals = Collections.synchronizedMap(new HashMap<PairBarrelClock, ArrayList<String>>());
				bufferSync = Collections.synchronizedMap(new HashMap<PairBarrelClock, PairIndRecParents>());
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
		}

		public void queryResponse(ArrayList<String> top10, Barrel barrel, int clock) throws RemoteException{
				bufferPals.put(new PairBarrelClock(barrel, clock), top10);
		}

		public void syncResponse(Map<String, Set<String>> indRec, Map<String, Set<String>> parents, Barrel barrel, int clock) throws RemoteException{
				bufferSync.put(new PairBarrelClock(barrel, clock), new PairIndRecParents(indRec, parents));
		}

}
