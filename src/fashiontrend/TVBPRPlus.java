package fashiontrend;

public class TVBPRPlus {
	
	public TVBPRPlus(Corpus corpus, int k, int k2, double lambda, double lambda2, double biasReg, double nEpoch, 
			                        int iter, String corp_name)
	{
		System.out.println("Inside TVBPRPlus constructor");
		new TVBPR(corpus, k, k2, lambda, lambda2, biasReg, nEpoch);
		System.out.println("TVBPRPlus constructor Done");
		
	}

	public void init(String corp_name) {
		
	}

}
