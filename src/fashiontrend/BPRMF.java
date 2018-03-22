package fashiontrend;

import java.io.IOException;

public class BPRMF {

	public BPRMF(Corpus corpus, int k, double lambda, double biasReg) {
		System.out.println("Inside BPRMF constructor");
		new Model(corpus);
		System.out.println("BPRMF constructor Done");
	}

}
