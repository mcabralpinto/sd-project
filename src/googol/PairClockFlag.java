package googol;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Classe que guarda um Stamp de comunicação e uma flag para Barrels
 * Flag: 0 -> AM, unsynchronized; 1 -> AM, synchronized; 2 -> NZ, unsynchronized; 3 -> NZ, synchronized
 * @author Joao Nuno Coelho
 * @version 1.0
 */
class PairClockFlag{
		AtomicLong clock;
		int flag;

		PairClockFlag(int clock, int flag){
				this.clock = new AtomicLong(clock);
				this.flag = flag;
		}
}
