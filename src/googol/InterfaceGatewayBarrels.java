package googol;

import java.rmi.*;
import java.util.*;

public interface InterfaceGatewayBarrels extends Remote {
		void subscribe(Barrel barrel) throws RemoteException;
		void acknowledgeUpdate(Barrel barrel, int clock) throws RemoteException;
		void queryResponse(ArrayList<String> top10, Barrel barrel, int clock) throws RemoteException;
		void syncResponse(Map<String, Set<String>> indRec, Map<String, Set<String>> parents, Barrel barrel, int clock) throws RemoteException;
}
