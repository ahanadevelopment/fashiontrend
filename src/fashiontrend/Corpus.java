package fashiontrend;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import javafx.util.*;


public class Corpus {
	
	Vector<Vote> V = new Vector<Vote>(); // vote
	
	int nUsers = 0; // Number of users
	int nItems = 0; // Number of users
	int nVotes; // Number of ratings
	Map<String, Integer> itemIds = new HashMap<String, Integer>(); // Maps an item's string-valued ID to an integer
	
	public void loadData(String reviewPath, String imgFeatPath, int userMin, int itemMin) throws IOException 
	{
		System.out.println("Inside loadData() in Corpus");
		
		loadVotes(reviewPath, imgFeatPath, userMin, itemMin);
		//loadImgFeatures(imgFeatPath);
	}
	
	private void loadVotes(String reviewPath, String imgFeatPath, int userMin, int itemMin) throws IOException 
	{
		System.out.println("Inside loadVotes() in Corpus");
         Map<String, Integer> imgAsins = new HashMap<String, Integer>();
         int counter = 0;
 	    
 	        
	    try {
	    	System.out.println("Reading file : " + imgFeatPath);
	    	  File imageFile = new File(imgFeatPath);
		      DataInputStream dis = new DataInputStream(new FileInputStream(imageFile)); //to read the file
		      byte[] b = new byte[(int)imageFile.length()]; //to store the bytes
		      int l = dis.read(b); //stores the bytes in b
		      System.out.println("Length = " + l);
		      
		      byte[] temp = new byte[10];
		      List<String> asinList = new ArrayList<String>();
		      
		      
		      for (int i=0;i<b.length; i++) {
		    	  
		    	  if(b[i] == 66) {
		    		  if (b[i+1] == 48 && b[i+2] == 48) {
		    			  int k = 0;
		    			  for (int j=i; j<i+10; j++) {
		    				  temp[k] = b[j];
		    				  k++;
		    			  }
		    			
		    			  if (checkAscii(temp)) {
		    				  //asinList.add(new String(temp));
		    				  imgAsins.put(new String(temp), 1);
		    				  counter ++;
		    			  }
		    			  
		    		  }
		    	  }
		    	  
		    	  else if((48 < b[i]) && (b[i] < 58)) {
		    		  
		    			  int k = 0;
		    			  for (int j=i; j<i+10; j++) {
		    				  temp[k] = b[j];
		    				  k++;
		    			  }
		    			  
		    			  if (checkISBN(temp)) {
		    				  //asinList.add(new String(temp));
		    				  imgAsins.put(new String(temp), 1);
		    				  counter ++;
		    			  }
		    			  
		    	  }
		    	 
		    	 
		      }
		      System.out.println("Count = " + counter);
		      
		      
		      dis.close();
		    } catch (IOException e) {
		      e.printStackTrace(System.err);
		    }
		
	    
		String uName; // User name
		String bName; // Item name
		float value; // Rating
		long voteTime; // Time rating was entered
		Map<Pair<Integer, Integer>, Long> voteMap = new HashMap<Pair<Integer,Integer>, Long>();
		/* For pre-load */
    	Map<String, Integer> uCounts = new HashMap<String, Integer>();
    	Map<String, Integer> bCounts = new HashMap<String, Integer>();
    	
    	Map<String, Integer> userIds = new HashMap<String, Integer>(); // Maps a user's string-valued ID to an integer
    	//Map<String, Integer> itemIds = new HashMap<String, Integer>(); // Maps an item's string-valued ID to an integer
    	Map<Integer, String> rUserIds = new HashMap<Integer, String>(); // Inverse of the above maps
    	Map<Integer, String> rItemIds = new HashMap<Integer, String>();

		int nRead = 0; // Progress
		String line;
		
		GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(reviewPath));
		BufferedReader br = new BufferedReader(new InputStreamReader(gzip));
		
		if(!br.ready()) {
			System.exit(1);
		}
		
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			Object[] token = line.split(" ");
			uName = (String)token[0];
			bName = (String)token[1];
	        value = Float.valueOf((String)token[2]);
	        System.out.print(uName + " ");
	        System.out.print(bName + " ");
	        System.out.println(value);
	        
	        nRead++;
	        
	        if (nRead % 100000 == 0) {
	        	System.out.println("Why we need this if loop. Any bearing in functionality? ");
			}
	        
	        if (!imgAsins.containsKey(bName)) {
				//continue;  // Do we need this continue statement
			}
	        
	        if (value > 5 || value < 0) { // Ratings should be in the range [0,5]
	        	System.out.println("Got bad value of %f\nOther fields were %s %s\n" + value + " " + uName + " " + bName);
				System.exit(1); // Why we exit if we get a bad value
			}
	        
	        
	    	
	    	if (!uCounts.containsKey(uName)) {
				uCounts.put(uName, 0);
			}
	    	
			if (!bCounts.containsKey(bName)) {
				bCounts.put(bName, 0);
			}
			
			uCounts.put(uName, uCounts.get(uName) + 1);
			bCounts.put(bName, bCounts.get(bName) + 1);    	
	    	
		}
		    br.close();
			gzip.close();
			
			
			// Re-read
			
			
			
			Vector<Vector<Double> > ratingPerItem = new Vector<Vector<Double>>();
			Vector<Integer> numReviewsPerItem = new Vector<Integer>();
			Vector<Double> avgRatingPerItem = new Vector<Double>();
			nRead = 0;
			
			GZIPInputStream gzip2 = new GZIPInputStream(new FileInputStream(reviewPath));
			BufferedReader br2 = new BufferedReader(new InputStreamReader(gzip2));
			
			if(!br2.ready()) {
				System.exit(1);
			}
			
			while ((line = br2.readLine()) != null) {
				System.out.println(line);
				Object[] token = line.split(" ");
				uName = (String)token[0];
				bName = (String)token[1];
		        value = Float.valueOf((String)token[2]);
		        voteTime = Long.valueOf((String)token[3]);
		        System.out.print(uName + " ");
		        System.out.print(bName + " ");
		        System.out.print(value + " ");
		        System.out.println(voteTime + " ");
		        
		        nRead++;
		        
		        if (nRead % 100000 == 0) {
		        	System.out.println("Why we need this if loop. Any bearing in functionality? ");
				}
		        
		        if (!imgAsins.containsKey(bName)) {
					//continue;  // Do we need this continue statement
				}
		        
		        if (uCounts != null && bCounts != null) {
		        if (uCounts.get(uName) < userMin || bCounts.get(bName) < itemMin) {
		        	System.out.println("inside ok");
		        	//continue;  // Do we need this continue statement
		        }
		        }
		    	
		     // new item
				if (!itemIds.containsKey(bName)) {
					rItemIds.put(nItems, bName);
					itemIds.put(bName, nItems++);
					Vector<Double> vec = new Vector<Double>();
					ratingPerItem.add(vec);
				} 
		        
		     // new user
				if (!userIds.containsKey(uName)) {
					rUserIds.put(nUsers, uName);
					userIds.put(uName, nUsers++);
				}
				
				if(itemIds.containsKey(bName)) {
					
				}
				
				
				ratingPerItem.elementAt(itemIds.get(bName)).add((double) value);
				voteMap.put(new Pair(userIds.get(uName), itemIds.get(bName)), voteTime);
		    	
			}
			    br2.close();
				gzip2.close();
			
				
				for (int x = 0; x < nItems; x ++) {
					numReviewsPerItem.add(ratingPerItem.elementAt(x).size());
					double sum = 0;
					for (int j = 0; j < (int)ratingPerItem.elementAt(x).size(); j ++) {
						sum += ratingPerItem.elementAt(x).elementAt(j);
					}
					if (ratingPerItem.elementAt(x).size() > 0) {
						avgRatingPerItem.add(sum / ratingPerItem.elementAt(x).size());
					} else {
						avgRatingPerItem.add((double) 0);
					}
				}
		
				generateVotes(voteMap);
		
	}
	
	private void generateVotes(Map<Pair<Integer, Integer>, Long> voteMap)
	{
		System.out.println("Inside generateVotes() in Corpus");
		
		for (Map.Entry<Pair<Integer, Integer>, Long> entry : voteMap.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            Vote v = new Vote();
			v.setUser(entry.getKey().getKey());
			v.setItem(entry.getKey().getValue());
			v.setVoteTime(entry.getValue());
			v.setLabel(1); // positive
			V.add(v);
        }
		
		nVotes = V.size();
		Collections.shuffle(V);
					
	}
	
	private void loadImgFeatures(String imgFeatPath) 
	{
		System.out.println("Inside loadImgFeatures() in Corpus");
		Vector<Vector<Pair<Integer, Float>> > imageFeatures = new Vector<Vector<Pair<Integer,Float>>>();
		
		for (int i = 0; i < nItems; i ++) {
			Vector<Pair<Integer, Float> > vec = new Vector<Pair<Integer,Float>>();
			imageFeatures.add(vec);
		}
		
		double ma = 58.388599; // Largest feature observed
		
		try {
	    	System.out.println("Reading file : " + imgFeatPath);
	    	  File imageFile = new File(imgFeatPath);
		      DataInputStream dis = new DataInputStream(new FileInputStream(imageFile)); //to read the file
		      byte[] b = new byte[(int)imageFile.length()]; //to store the bytes
		      int l = dis.read(b); //stores the bytes in b
		      System.out.println("Length = " + l);
		      int counter = 0;
		      
		      byte[] temp = new byte[10];		      
		      
		      for (int i=0;i<b.length; i++) {
		    	  
		    	  if(b[i] == 66) {
		    		  if (b[i+1] == 48 && b[i+2] == 48) {
		    			  int k = 0;
		    			  for (int j=i; j<i+10; j++) {
		    				  temp[k] = b[j];
		    				  k++;
		    			  }
		    			  
		    			  if (checkAscii(temp)) {
		    				  if (!itemIds.containsKey(new String(temp))) {
		    					 // continue; // How does it work?
		    					/*  Vector<Pair<Integer, Float>> vec = imageFeatures.at(itemIds.get(new String(temp)));
		    						for (int f = 0; f < imFeatureDim; f ++) {
		    							if (feat[f] != 0) {  // compression
		    								vec.add(new Pair(f, feat[f]/ma));
		    							}
		    						} */
		    					 
		    						counter++;
		    				  }
		    			  }
		    		  }
		    	  }
		    	  
		    	  else if((48 < b[i]) && (b[i] < 58)) {
		    		  
		    			  int k = 0;
		    			  for (int j=i; j<i+10; j++) {
		    				  temp[k] = b[j];
		    				  k++;
		    			  }
		    			  
		    			  if (checkISBN(temp)) {
		    				  if (!itemIds.containsKey(new String(temp))) {
			    					 // continue; // How does it work?
		    					  counter++;
			    			}
		    		  }
		    			  
		    	  }
		    	 
		    	  
		      }
		      System.out.println("Count = " + counter);
		      
		      
		      dis.close();
		    } catch (IOException e) {
		      e.printStackTrace(System.err);
		    }
		
				
	}
	
		
	private boolean checkAscii(byte[] c) {
		boolean isAscii = false;
		for (int i = 0; i < c.length; i++) {
			if (((64 < c[i]) && (c[i] < 91)) || ((47 < c[i]) && (c[i] < 58))) {
				isAscii = true;
			}
			else {
				isAscii = false;
			}
		}
		
		return isAscii;
	}
   
	private boolean checkISBN(byte[] c) {
		boolean isAscii = false;
		for (int i = 0; i < c.length; i++) {
			if ((i == 0) && ((48 < c[i]) && (c[i] < 58))) {
				isAscii = true;
			}
			else if ((i > 0) && ((47 < c[i]) && (c[i] < 58))) {
				isAscii = true;
			}
			else {
				isAscii = false;
				break;
			}
		}
		
		return isAscii;
	}

}
