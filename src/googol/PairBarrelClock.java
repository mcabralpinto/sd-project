package googol;

/**
 * Classe que guarda um Barrel e um valor de Stamp de comunicação
 * @author Joao Nuno Coelho
 * @version 1.0
 */
class PairBarrelClock{
		Barrel barrel;
		long clock;

		PairBarrelClock(Barrel barrel, long clock){
				this.barrel =  barrel;
				this.clock = clock;
		}
}
