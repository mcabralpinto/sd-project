package googol;

import java.rmi.*;
import java.util.*;

public interface InterfaceBarrel extends Remote{
		void syncRequestQuery(long curClock) throws RemoteException;
		void syncAnswer(Map<String, Set<String>> indRec, Map<String, Set<String>> parents, long curClock) throws RemoteException;
		void updateAnswer(Map<String, ArrayList<String>> answer, long curClock) throws RemoteException;
		void wordQuery(String word, long curClock) throws RemoteException;
}
