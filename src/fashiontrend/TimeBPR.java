package fashiontrend;

import java.io.IOException;
import java.util.Vector;

import javafx.util.Pair;

public class TimeBPR {

	public TimeBPR(Corpus corpus, int k, double lambda, double biasReg, double nEpoch) {
		System.out.println("Inside TimeBPR constructor");
		new BPRMF(corpus, k, lambda, biasReg);
		
		
		int nBin = 80;
		
		Epoch epochs[];

		// calculate MIN & MAX
		long voteTime_min = Integer.MAX_VALUE;
		long voteTime_max = Integer.MIN_VALUE;
		
		for (int x = 0; x < corpus.nVotes; x++) {
			Vote vi = corpus.V.elementAt(x);
			long voteTime = vi.voteTime;
			if (voteTime < voteTime_min) {
				voteTime_min = voteTime;
			}
			if (voteTime > voteTime_max) {
				voteTime_max = voteTime;
			}
		}
		
		   // calculate pos_item_per_bin
		        
		        Vector<Pair<Integer, Integer>>[] votes_per_bin = (Vector<Pair<Integer, Integer>>[]) new Vector[nBin];
		        for(int i = 0; i < votes_per_bin.length; i++) {
		        	votes_per_bin[i] = new Vector<Pair<Integer, Integer>>();
		        }
		        
				long interval = (voteTime_max - voteTime_min) / nBin;
				for (int u = 0; u < corpus.nUsers; u++) {
					if (corpus.val_per_user[u].getKey() != -1) {
						int item = corpus.val_per_user[u].getKey();
						long voteTime = corpus.val_per_user[u].getValue();
						int bin_idx = Math.min(nBin - 1, (int)((voteTime - voteTime_min) / interval));
						votes_per_bin[bin_idx].add(new Pair(u, item));
					}
				} 
				
				
				// initialize epochs
				epochs = new Epoch[(int) nEpoch];
				for(int i = 0; i < epochs.length; i++) {
					epochs[i] = new Epoch();
		        }
				
				interval = nBin / (int)nEpoch;
				int bin_to = nBin - 1;
				for (int ep = (int)nEpoch - 1; ep >= 0; ep --) {
					epochs[ep].bin_to = bin_to;
					if (ep == 0) {
						epochs[ep].bin_from = 0;
						break;
					} else {
						epochs[ep].bin_from = epochs[ep].bin_to - (int)interval + 1;
						bin_to = epochs[ep].bin_from - 1;
					}
				}
				
				
				System.out.println("\n===== Initial Epoch segmentation =====\n");
				for (int ep = 0; ep < nEpoch; ep ++) {
					System.out.println("Epoch " + ep + ": " + epochs[ep].bin_from + " " + epochs[ep].bin_to);
				}
				System.out.println("=======================================\n\n");
				
				
				// init DP
				memo = new double*** [nBin];
				sol = new int*** [nBin];
				for (int i = 0; i < nBin; i ++) {
					memo[i] = new double** [nBin];
					sol[i] = new int** [nBin];
					for (int j = 0; j < nBin; j ++) {
						memo[i][j] = new double* [nEpoch];
						sol[i][j] = new int* [nEpoch];
						for (int k = 0; k < nEpoch; k ++) {
							memo[i][j][k] = new double[nEpoch];
							sol[i][j][k] = new int[nEpoch];
						}
					}
				}
				
		
		System.out.println("TimeBPR constructor Done");
		
	}

}
