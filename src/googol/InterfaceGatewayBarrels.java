package googol;

import java.rmi.*;
import java.util.*;

/**
 * Interface que explicita os métodos que podem ser usados pelos Barrel para comunicar com a gateway
 * @author Joao Nuno Coelho
 * @version 1.0
 */
public interface InterfaceGatewayBarrels extends Remote {
		/**
		 * Método, a ser usado por barrels, que subscreve um barrel à gateway.
		 * O Barrel só é subscrito se não existir. O barrel fica AM ou NZ dependendo do tipo do último barrel subcrito.
		 * @param barrel Barrel a ser subscrito à gateway
		 * @throws RemoteException
		 */
		void subscribe(Barrel barrel) throws RemoteException;
		/**
		 * Método, a ser usado por barrels, que informa a gateway que o barrel para o qual enviou a mensagem, a recebeu.
		 * @param barrel Barrel que recebeu a mensagem e está a informar a gateway
		 * @param clock Stamp da mensagem
		 * @throws RemoteException
		 */
		void acknowledgeUpdate(Barrel barrel, int clock) throws RemoteException;
		/**
		 * Método, a ser usado por barrels, que envia a resposta à gateway do pedido que esta fez sobre uma palavra
		 * @param top10 Resposta do barrel, top10 sites mais importantes envolvendo a palavra do pedido
		 * @param barrel Barrel a responder à gateway
		 * @param clock Stamp da mensagem
		 * @throws RemoteException
		 */
		void queryResponse(ArrayList<String> top10, Barrel barrel, int clock) throws RemoteException;
		/**
		 * Método, a ser usado por barrels, que envia a resposta à gateway do pedido que esta fez para sincronizar barrels
		 * @param indRec Indice recursivo do barrel
		 * @param parents Pais de todos os urls do barrel
		 * @param barrel Barrel a responder à gateway
		 * @param clock Stamp da mensagem
		 * @throws RemoteException
		 */
		void syncResponse(Map<String, Set<String>> indRec, Map<String, Set<String>> parents, Barrel barrel, int clock) throws RemoteException;
}
