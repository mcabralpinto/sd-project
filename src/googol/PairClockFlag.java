package googol;

class PairClockFlag{
		int clock;
		//0 -> AM, unsynchronized; 1 -> AM, synchronized; 2 -> NZ, unsynchronized; 3 -> NZ, synchronized
		int flag;

		PairClockFlag(int clock, int flag){
				this.clock = clock;
				this.flag = flag;
		}
}
