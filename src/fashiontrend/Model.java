package fashiontrend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;

public class Model {
	
	int nUsers; // Number of users
	int nItems; // Number of items
	int nVotes; // Number of ratings

	public Model(Corpus corpus) {
		System.out.println("Inside Model constructor");
		
		nUsers = corpus.nUsers;
		nItems = corpus.nItems;
		nVotes = corpus.nVotes;
		
	    //List<Pair<Integer, Long>> test_per_user = new ArrayList<Pair<Integer, Long>>(nUsers);
	    //List<Pair<Integer, Long>> val_per_user = new ArrayList<Pair<Integer, Long>>(nUsers);
	    
	    Pair<Integer, Long>[] test_per_user = (Pair<Integer, Long>[])new Pair[nUsers];
	    Pair<Integer, Long>[] val_per_user = (Pair<Integer, Long>[])new Pair[nUsers];
	    
	    Map<Integer, Long> pos_per_user = new HashMap<Integer, Long>(nUsers);
	    Map<Integer, Long> pos_per_item = new HashMap<Integer, Long>(nItems);
	    
	   /* for (int u = 0; u < nUsers; u++) {
			test_per_user.add(new Pair(-1, -1));  // -1 denotes empty
			val_per_user.add(new Pair(-1, -1));
		} */
	    
	    for (int u = 0; u < nUsers; u ++) {
			test_per_user[u] = new Pair(-1, -1);  // -1 denotes empty
			val_per_user[u]  = new Pair(-1, -1);
		}
	    
	   // split into training set AND valid set AND test set 
	   // NOTE: never use corp->V as the training set
	 		//pos_per_user = new map<int,long long>[nUsers];
	 		//pos_per_item = new map<int,long long>[nItems];
	 		for (int x = 0; x < nVotes; x ++) {
	 			Vote v = corpus.V.elementAt(x);
	 			int user = v.user;
	 			int item = v.item;
	 			long voteTime = v.voteTime; 

	 			if (test_per_user[user].getKey() == -1) { // add to test set
	 				test_per_user[user] = new Pair(item, voteTime);
	 			} else if (val_per_user[user].getKey() == -1) { // add to validation set
	 				val_per_user[user] = new Pair(item, voteTime);
	 			}
	 			else {// add to training set
	 				pos_per_user[user][item] = voteTime;
	 				pos_per_item[item][user] = voteTime;
	 			}
	 		} 
	    
	    
	}

}
