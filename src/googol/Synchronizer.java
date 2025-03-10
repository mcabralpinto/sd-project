package googol;

/**
 * Classe que serve para criar uma Thread que lida apenas com a sincronização dos barrels
 * @author Joao Nuno Coelho
 * @version 1.0
 */
class Synchronizer implements Runnable{
		GatewayBarrels gateway;
		int syncThreshold;
		int tries;
		long timeout;
		Thread t;
		/**
		 * Construtor da Classe que inicia a Thread
		 * @param gateway Gateway para a qual irá manter a sincronização
		 */
		Synchronizer(GatewayBarrels gateway, int syncThreshold, int tries, long timeout){
				this.gateway = gateway;
				this.syncThreshold = syncThreshold;
				this.tries = tries;
				this.timeout = timeout;
				t = new Thread(this, "synchronizer");
				t.start();
		}

		/**
		 * Método obrigatório numa Clase que implementa a interface Runnable
		 * Se 5 em 5 minutos, sincroniza
		 */
		public void run(){
				try{
						while (true){
								Thread.sleep(this.syncThreshold);
								gateway.synchronize(this.tries, this.timeout);
						}
				} catch (InterruptedException e){
						System.out.println("Synchronization stopped");
				}
		}
}

