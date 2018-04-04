package fashiontrend;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.NClob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import com.sun.org.apache.xerces.internal.xs.ItemPSVI;

import javafx.util.Pair;

public class Model {
	
	int nUsers; // Number of users
	static int nItems; // Number of items
	int nVotes; // Number of ratings
	
	/* Category information */
	static int[] itemCategoryId;
	static Map<Integer, Integer> nodeIds = new HashMap<Integer, Integer>();
	static Map<Integer, String> rNodeIds = new HashMap<Integer, String>();
	static int nCategory;
	
	/* additional information for demo paper */
	static Map<Integer, Double> itemPrice = new HashMap<Integer, Double>();
	static Map<Integer, String> itemBrand = new HashMap<Integer, String>();
	
	static Corpus corpus1;

	public static Corpus getCorpus1() {
		return corpus1;
	}

	public void setCorpus1(Corpus corpus1) {
		this.corpus1 = corpus1;
	}

	public Model(Corpus corpus) {
		System.out.println("Inside Model constructor");
		
		nUsers = corpus.nUsers;
		nItems = corpus.nItems;
		nVotes = corpus.nVotes;
		
		int num_pos_events = 0;
		
	    //List<Pair<Integer, Long>> test_per_user = new ArrayList<Pair<Integer, Long>>(nUsers);
	    //List<Pair<Integer, Long>> val_per_user = new ArrayList<Pair<Integer, Long>>(nUsers);
	    
	    Pair<Integer, Long>[] test_per_user = (Pair<Integer, Long>[])new Pair[nUsers];
	    corpus.setVal_per_user(nUsers);
	    //Pair<Integer, Long>[] val_per_user = (Pair<Integer, Long>[])new Pair[nUsers];
	    
	    
	   // Map<Integer, Long> pos_per_user = new HashMap<Integer, Long>(nUsers);
	   // Map<Integer, Long> pos_per_item = new HashMap<Integer, Long>(nItems);
	    
	    Map<Pair<Integer, Long>, Long> pos_per_user = new HashMap<Pair<Integer, Long>, Long>(nUsers);
	    Map<Pair<Integer, Long>, Long> pos_per_item = new HashMap<Pair<Integer, Long>, Long>(nItems);
	    
	   /* for (int u = 0; u < nUsers; u++) {
			test_per_user.add(new Pair(-1, -1));  // -1 denotes empty
			val_per_user.add(new Pair(-1, -1));
		} */
	    
	    System.out.println("Inside Model constructor : " + nUsers);
	    //System.out.println("Inside Model constructor corpus : " + corpus.nUsers);
	    
	    for (int u = 0; u < nUsers; u ++) {
	    	//System.out.println("Inside Model for loop : " + corpus.val_per_user.length);
			test_per_user[u] = new Pair(-1, -1);  // -1 denotes empty
			corpus.val_per_user[u]  = new Pair(-1, -1);
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
	 			} else if (corpus.val_per_user[user].getKey() == -1) { // add to validation set
	 				corpus.val_per_user[user] = new Pair(item, voteTime);
	 			}
	 			else {// add to training set
	 				//pos_per_user[user][item] = voteTime;
	 				pos_per_user.put(new Pair(user, item), voteTime);
	 				pos_per_item.put(new Pair(item, user), voteTime);
	 			}
	 		} 
	 		
	 	// sanity check
			for (int u = 0; u < nUsers; u ++) {
				if (test_per_user[u].getKey() == -1 || corpus.val_per_user[u].getKey() == -1) {
					System.out.println("\n Corpus split exception.\n");
					System.exit(1);
				}
			}
			
			// calculate num_pos_events
			
		/*	for (int u = 0; u < nUsers; u ++) {
				
					for (Map.Entry<Pair<Integer, Long>, Long> entry : pos_per_user.entrySet()) {
						if(entry.getKey().getKey().equals(obj))
					}
				num_pos_events += pos_per_user.get(u).SIZE;
			} */
	    
	    setCorpus1(corpus);
			System.out.println("Model constructor Done");
	}
	
	public static void loadCategories(String categoryPath, String subcategoryName, String rootName, boolean skipRoot) {
		System.out.println("Inside loadCategories method in Model class");
		
		itemCategoryId = new int [nItems];
		for (int i = 0; i < nItems; i ++) {
			itemCategoryId[i] = -1;
		}
		
		System.out.println("Loading category data");
		CategoryTree ct = new CategoryTree(rootName, skipRoot);
		
		GZIPInputStream gzip3 = null;
		BufferedReader br3 = null;
		try {
			gzip3 = new GZIPInputStream(new FileInputStream(categoryPath));
			br3 = new BufferedReader(new InputStreamReader(gzip3));
			if(!br3.ready()) {
				System.exit(1);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String line;
		int item = -1;
		int count = 0;
		nCategory = 0;
		
		try {
			while ((line = br3.readLine()) != null) {
				
				if(line.split(" ").length > 0 && !line.split(" ")[0].isEmpty()) {
					String itemId;
					double price = -1;
					String brand = "unknown_brand";
					itemId = line.split(" ")[0];
					if(!(line.split(" ")[1]).isEmpty()) {
					price = Double.valueOf(line.split(" ")[1]);
					}
					
					if (!corpus1.itemIds.containsKey(itemId)) {
						item = -1;
						continue;
					}
					
					item = corpus1.itemIds.get(itemId);
					
					itemPrice.put(item, price);
					itemBrand.put(item, brand);
					
					count++;
					if (count % 10000 == 0) {
						System.out.println("."); // Do we need this
					}
					continue;
				}
				
					if (item == -1) {
						continue;
					}
					
					
					Vector<String> category = new Vector<String>();

					for (String str: line.split(",")) {
						category.add(str);
					}
					
					if (category.get(0) != "Clothing Shoes & Jewelry" || category.get(1) != subcategoryName) {
						continue;
					}
					
					ct.addPath(category);

					if (category.size() < 4) {
						continue;
					}
					
					String categoryP = category.get(0);
					int targetNode = ct.find(categoryP, 4); // Need change ????
					if (targetNode == 0) {
						System.out.println("Can't find the category node.\n");
						System.exit(1);
					}
					
					
					if (!nodeIds.containsKey(targetNode)) {
						nodeIds.put(targetNode, nCategory);
						rNodeIds.put(nCategory, category.get(2) + "," + category.get(3));
						nCategory ++;
					}
					
					itemCategoryId[item] = nodeIds.get(targetNode);
					
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		try {
			br3.close();
			gzip3.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int total = 0;
		for (int i = 0; i < nItems; i ++) {
			if (itemCategoryId[i] != -1) {
				total ++;
			}
		}
		
		System.out.println(" #Items with category: " + total);
		if (1.0 * total / nItems < 0.5) {
			System.out.println("So few items are having category info. Sth wrong may have happened.");
			System.exit(1);
		}
		
		System.out.println("LoadCategories method in Model class Done");
	}
	

}
