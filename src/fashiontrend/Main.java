package fashiontrend;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		
		String reviewPath = "";
		String imgFeatPath = "";
		int k;
		int k2;
		double alpha;
		double biasReg;
		double lambda;
		double lambda2;
		double nEpoch;
		int iter;
		String corp_name;
		
		if (args.length == 11)
		{
			System.out.print(" Parameters are passed in below order: \n");
			System.out.print(" 1. Review file path\n");
			System.out.print(" 2. Img feature path\n");
			System.out.print(" 3. Latent Feature Dim. (K)\n");
			System.out.print(" 4. Visual Feature Dim. (K')\n");
			System.out.print(" 5. alpha (for WRMF only)\n");
			System.out.print(" 6. biasReg (regularizer for bias terms)\n");
			System.out.print(" 7. lambda  (regularizer for general terms)\n");
			System.out.print(" 8. lambda2 (regularizer for \"sparse\" terms)\n");
			System.out.print(" 9. #Epoch (number of epochs) \n");
			System.out.print("10. Max #iter \n");
			System.out.print("11. Corpus/Category name under \"Clothing Shoes & Jewelry\" (e.g. Women)\n\n");
			
			reviewPath = args[0];
			imgFeatPath = args[1];
			k = Integer.parseInt(args[2]);
			k2 = Integer.parseInt(args[3]);
			alpha = Double.parseDouble(args[4]);
			biasReg = Double.parseDouble(args[5]);
			lambda = Double.parseDouble(args[6]);
			lambda2 = Double.parseDouble(args[7]);
			nEpoch = Integer.parseInt(args[8]);
			iter = Integer.parseInt(args[9]);
			corp_name = args[10];
		}
		else {
			throw new IllegalArgumentException("Arguments passed are not in order. Total 11 aruguments needed."
					+ "But " + args.length + " arguments passed");
		}
		
		Corpus corpus = new Corpus();
		corpus.loadData(reviewPath, imgFeatPath, 5, 0);
		
		TVBPRPlus md = new TVBPRPlus(corpus, k, k2, lambda, lambda2, biasReg, nEpoch, iter, corp_name);
		md.init(corp_name);

	}

}
