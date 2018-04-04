package fashiontrend;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CategoryNode {
	
	static Map<String, Integer> children;
	Set<Integer> productSet;
	
	/*CategoryNode(String name, int parent, int nodeId)
	{
		
		children = new HashMap<String, Integer>();
		productSet = new HashSet<Integer>();
		
	}*/
	
 public int getCategoryNode(String name, int parent, int nodeId)
	{
		children = new HashMap<String, Integer>();
		productSet = new HashSet<Integer>();
		return nodeId;
	}
	
	

}
