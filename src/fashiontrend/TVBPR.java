package fashiontrend;


public class TVBPR {

	public TVBPR(Corpus corpus, int k, int k2, double lambda, double lambda2, double biasReg, double nEpoch) {
		System.out.println("Inside TVBPR constructor");
		new TimeBPR(corpus, k, lambda2, biasReg, nEpoch);
		System.out.println("TVBPR constructor Done");
	}

}
