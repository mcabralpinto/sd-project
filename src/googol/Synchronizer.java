class Synchronizer implements Runnable{
		GatewayBarrels gateway;
		Thread t;
		Synchronizer(GatewayBarrels gateway){
				this.gateway = gateway;
				t = new Thread(this, "synchronizer");
				t.start();
		}

		public void run(){
				try{
						while (true){
								Thread.sleep(300000);
								gateway.synchronized();
						}
				} catch (InterruptedException e){
						System.out.println("Synchronization stopped");
				}
		}
}

