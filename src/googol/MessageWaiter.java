package googol;

import java.util.*;

/**
 * Classe que espera por messagens que deviam ter chegado a um barrel
 * @author Joao Nuno Coelho
 * @version 1.0
 */
class MessageWaiter implements Runnable{
		Barrel barrel;
		int waitNumber;
		long waitTime;
		Thread t;

		/**
		 * Construtor da classe, inicia a thread que espera pela messagem com Stamp waitNumber
		 * @param barrel Barrel para onde devia ir a mensagem
		 * @param waitNumber Stamp da messagem que deve chegar
		 * @param waitTime tempo máximo de espera pela mensagem
		 */
		MessageWaiter(Barrel barrel, int waitNumber, long waitTime){
				this.barrel = barrel;
				this.waitNumber = waitNumber;
				this.waitTime = waitTime;
				t = new Thread(this, "waiter " + Integer.toString(waitNumber));
				t.start();
		}

		/**
		 * Método obrigatório numa classe que implementa a interface Runnable
		 * Caso a mensagem chegue ou ultrapasse o seu timeout, a thread termina
		 */
		public void run(){
				long maxTime = System.currentTimeMillis() + waitTime;
				long curTime = System.currentTimeMillis();
				while (!barrel.getLateMessages(waitNumber) && barrel.getClock() < waitNumber && maxTime - curTime > 0){
						try{
								wait(maxTime - curTime);
						} catch (InterruptedException e){
								e.printStackTrace();
						}
						curTime = System.currentTimeMillis();
				}
				if (!barrel.getLateMessages(waitNumber) && barrel.getClock() < waitNumber){
						System.out.println("Message " + Integer.toString(waitNumber) + " lost");
				}
				barrel.removeLateMessages(waitNumber);
		}
}
