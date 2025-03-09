package googol;

import java.util.concurrent.atomic.AtomicInteger;

class PairClockFlag{
		AtomicInteger clock;
		//0 -> AM, unsynchronized; 1 -> AM, synchronized; 2 -> NZ, unsynchronized; 3 -> NZ, synchronized
		int flag;

		PairClockFlag(int clock, int flag){
				this.clock = new AtomicInteger(clock);
				this.flag = flag;
		}
}
