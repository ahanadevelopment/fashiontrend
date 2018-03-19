package fashiontrend;

import java.io.IOException;

public class TimeBPR {

	public TimeBPR(Corpus corpus, int k, double lambda, double biasReg, double nEpoch) {
		System.out.println("Inside TimeBPR constructor");
		new BPRMF(corpus, k, lambda, biasReg);
		
	}

}
