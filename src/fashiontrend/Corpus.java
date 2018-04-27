package fashiontrend;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang.ArrayUtils;

import javafx.util.*;


public class Corpus {
	
	Vector<Vote> V = new Vector<Vote>(); // vote
	static int counter1 = 0;
	Set<String> lst = new LinkedHashSet<>();
	static Map<String, Integer> imgAsins = new HashMap<String, Integer>();
	
	int nUsers = 0; // Number of users
	int nItems = 0; // Number of users
	int nVotes = 0; // Number of ratings
	Map<String, Integer> itemIds = new HashMap<String, Integer>(); // Maps an item's string-valued ID to an integer
	int imFeatureDim = 4096;
	//Pair<Integer, Long>[] val_per_user = (Pair<Integer, Long>[])new Pair[nUsers];
	Pair<Integer, Long>[] val_per_user;
	
	public Pair<Integer, Long>[] getVal_per_user() {
		return val_per_user;
	}

	public void setVal_per_user(int nusers) {
		this.val_per_user = (Pair<Integer, Long>[])new Pair[nusers];
	}

	public void loadData(String reviewPath, String imgFeatPath, int userMin, int itemMin) throws IOException 
	{
		System.out.println("Inside loadData() in Corpus");
		
		loadVotes(reviewPath, imgFeatPath, userMin, itemMin);
		loadImgFeatures(imgFeatPath);
		
		System.out.printf("nUsers = %d nItems = %d nVotes = %d", nUsers, nItems, nVotes );
		System.out.println();
	}
	
	private void loadVotes(String reviewPath, String imgFeatPath, int userMin, int itemMin) throws IOException 
	{
		System.out.println("Inside loadVotes() in Corpus");
		readImgPath(imgFeatPath);	
		
		System.out.println("Loading votes from " + reviewPath + "userMin = " + userMin + "itemMin = " + itemMin);
	    
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
			Object[] token = line.split(" ");
			uName = (String)token[0];
			bName = (String)token[1];
	        value = Float.valueOf((String)token[2]);
	        
	        nRead++;
	        
	        if (nRead % 100000 == 0) {
	        	System.out.println("Why we need this if loop. Any bearing in functionality? ");
			}
	        
	        if (!imgAsins.containsKey(bName)) {
				continue;
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
				//System.out.println(line);
				Object[] token = line.split(" ");
				uName = (String)token[0];
				bName = (String)token[1];
		        value = Float.valueOf((String)token[2]);
		        voteTime = Long.valueOf((String)token[3]);
		        //System.out.print(uName + " ");
		        //System.out.print(bName + " ");
		        //System.out.print(value + " ");
		        //System.out.println(voteTime + " ");
		        
		        nRead++;
		        
		        if (nRead % 100000 == 0) {
		        	System.out.println("Why we need this if loop. Any bearing in functionality? ");
				}
		        
		        if (!imgAsins.containsKey(bName)) {
					continue;
				}
		        
		        if (uCounts != null && bCounts != null) {
		        if (uCounts.get(uName) < userMin || bCounts.get(bName) < itemMin) {
		        	continue;
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
	
	private void readImgPath(String imgFeatPath) {
		System.out.println("Pre-loading image asins from " + imgFeatPath);
 	    
	    try {
	    	
	    	BufferedInputStream in = new BufferedInputStream(new FileInputStream(imgFeatPath));
	    	    byte[] bbuf = new byte[2000*2000];
	    	    int len; 
	    	    byte[] ret;
	    	    
	    	    while ((len = in.read(bbuf)) != -1) {
	    	    	ret = processBytesArray(bbuf);
	    	    }
	    	    	
	    } catch (Exception e) {
					e.printStackTrace();
				}
	    	
	    counter1 = imgAsins.size();
	    System.out.println("Counter size: " + counter1);
	    System.out.println("Map size: " + imgAsins.size());
		
	}
	
	private byte[] processBytesArray(byte[] bbuf) {
		
		int len = bbuf.length;
		byte[] temp1 = new byte[10];
		byte[] temp2 = null;
		
		for (int i=0; i<len; i++) {
    		
    		if (len-i > 9) {
    		if (bbuf[i] == 66 && bbuf[i+1] == 48 && bbuf[i+2] == 48) {
    			int k = 0;
    			  for (int j=i; j<i+10; j++) {
    				  temp1[k] = bbuf[j];
    				  k++;
    			  }
    			
    			  if (checkAscii(temp1)) {
    				  imgAsins.put(new String(temp1), 1);
    			  }
    	} 
    		else if ((47 < bbuf[i]) && (bbuf[i] < 58)) {
    			int k = 0;
  			  for (int j=i; j<i+10; j++) {
  				  temp1[k] = bbuf[j];
  				  k++;
  			  }
  			  
  			  if (checkISBN(temp1)) {
  				imgAsins.put(new String(temp1), 1);
  			  }
    		}
    	}
    		else {
    			
    			int p = 0;
    			temp2 = new byte[len-i];
    			  for (int z=i; z<len; z++) {
    				  temp2[p] = bbuf[z];
    				  p++;
    			  }
    			  
    			  break;
    		}	    	    		
	    			  
    	}
		
		//System.out.println("Tot cnt for bbuf length : " + len + " is " + counter1);
		temp1 = null;
		return temp2;
	}

	private void generateVotes(Map<Pair<Integer, Integer>, Long> voteMap)
	{
		System.out.println("Inside generateVotes() in Corpus");
		
		for (Map.Entry<Pair<Integer, Integer>, Long> entry : voteMap.entrySet()) {
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
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
		
		System.out.println("Loading image features from  " + imgFeatPath);
		
		for (int i = 0; i < nItems; i ++) {
			Vector<Pair<Integer, Float> > vec = new Vector<Pair<Integer,Float>>();
			imageFeatures.add(vec);
		}
		
		double ma = 58.388599; // Largest feature observed
		
        try {
	    	
	    	BufferedInputStream bin = new BufferedInputStream(new FileInputStream(imgFeatPath));
	    	    byte[] bbuf = new byte[2000*2000];
	    	    byte[] temp1 = new byte[10];
	    	    float[] feat = new float [imFeatureDim];
	    	    int len; 
	    	    byte[] ret;
	    	    String sAsin = "";
	    	    
	    	    while ((len = bin.read(bbuf)) != -1) {
	    	    	
	    	    	for (int i=0; i<len; i++) {
	    	    		
	    	    		if (len-i > 9) {
	    	    		if (bbuf[i] == 66 && bbuf[i+1] == 48 && bbuf[i+2] == 48) {
	    	    			int k = 0;
	    	    			  for (int j=i; j<i+10; j++) {
	    	    				  temp1[k] = bbuf[j];
	    	    				  k++;
	    	    			  }
	    	    			  sAsin = new String(temp1); 
	    	    			  if (!itemIds.containsKey(sAsin)) {
	  	    	    			continue;
	  	    	    		  }
	    	    			  Vector<Pair<Integer, Float> > ampvec = imageFeatures.elementAt(itemIds.get(sAsin));
	    	    			  for (int f = 0; f < imFeatureDim; f ++) {
	    	    					if (feat[f] != 0) {  // compression
	    	    						ampvec.add(new Pair(f, feat[f]/ma));
	    	    					}
	    	    				}
	    	    			
	    	    	  } 
	    	    		else if ((47 < bbuf[i]) && (bbuf[i] < 58)) {
	    	    			int k = 0;
	    	  			  for (int j=i; j<i+10; j++) {
	    	  				  temp1[k] = bbuf[j];
	    	  				  k++;
	    	  			  }
	    	  			sAsin = new String(temp1); 
  	    			  if (!itemIds.containsKey(sAsin)) {
	    	    			continue;
	    	    		  }
  	    			  Vector<Pair<Integer, Float> > ampvec = imageFeatures.elementAt(itemIds.get(sAsin));
  	    			  for (int f = 0; f < imFeatureDim; f ++) {
  	    					if (feat[f] != 0) {  // compression
  	    						ampvec.add(new Pair(f, feat[f]/ma));
  	    					}
  	    				}
	    	    		}
	    	    		
	    	    	}
	    	    			    	    		
	    		    			  
	    	    	}
	    	    	
	    	    }
	    	    	
	    } catch (Exception e) {
					e.printStackTrace();
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
				break;
			}
		}
		
		return isAscii;
	}
   
	private boolean checkISBN(byte[] c) {
		boolean isAscii = false;
		for (int i = 0; i < c.length; i++) {
			if ((i != 9) && (47 < c[i]) && (c[i] < 58)) {
				isAscii = true;
			}
			else if ((i == 9) && (((47 < c[i]) && (c[i] < 58)) || (c[i] == 88))) {
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
