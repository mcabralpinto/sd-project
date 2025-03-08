package googol;

import java.rmi.*;
import java.util.*;

public interface InterfaceGatewayBarrels extends Remote {
		void subscribe(BarrelAM barrel) throws RemoteException;
		void subscribe(BarrelNZ barrel) throws RemoteException;
		void acknowledgeUpdate(BarrelAM barrel, int clock) throws RemoteException;
		void acknowledgeUpdate(BarrelNZ barrel, int clock) throws RemoteException;
		void queryResponse(ArrayList<String> top10, BarrelAM barrel, int clock) throws RemoteException;
		void queryResponse(ArrayList<String> top10, BarrelNZ barrel, int clock) throws RemoteException;
		void syncResponse(Map<String, Set<String>> indRec, Map<String, Set<String>> parents, BarrelAM barrel, int clock) throws RemoteException;
		void syncResponse(Map<String, Set<String>> indRec, Map<String, Set<String>> parents, BarrelNZ barrel, int clock) throws RemoteException;
}
