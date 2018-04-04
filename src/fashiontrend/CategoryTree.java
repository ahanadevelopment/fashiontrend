package fashiontrend;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class CategoryTree {
	
	int root;
	Map<Integer, Integer> nodeToId;
	Vector<Integer> idToNode;
	boolean skipRoot;
	CategoryNode cn = new CategoryNode();
	
	CategoryTree(String rootName, boolean skipRoot)
	{
		
		root = cn.getCategoryNode(rootName, 0, 0);

		nodeToId = new HashMap<Integer, Integer>();
		idToNode = new Vector<Integer>();

		nodeToId.put(root, 0);
		idToNode.add(root);
	}
	
	public int addPath(Vector<String> category) {
		String categoryP = category.get(0);
		int child = 0;
		int deepest = root;
		int L = category.size();
		
		if (skipRoot) {
			//catElement ++; // This is to be done 
			L --;
			if (L == 0) {
				return root;
			}
		}
		
		while ( L>0 && (child == find(categoryP, 1))) {
			//catElement ++; // This is to be done 
			deepest = child;
			L --;
		}
		
		// We ran out of children. Need to add the rest.
		while (L > 0) {
			int nextId = (int) idToNode.size();

			/*child = cn.getCategoryNode(categoryP, deepest, nextId);
			//deepest->addChild(child);
			deepest = child;
			categoryP ++;
			L --;

			// Give each new child a new id. This code should be changed if we only want to give leaf nodes ids.
			nodeToId[child] = nextId;
			idToNode.push_back(child); */
		}
		
		return child;
	}
	
	int find(String categoryP, int L) {
		if (L == 0) {
			return L;
		}
		
		if (!CategoryNode.children.containsKey(categoryP)) {
			return 0;
		}
		
		return CategoryNode.children.get(categoryP);
		//return children[category[0]]->find(category + 1, L - 1);
	}

	private void addChild(int child)
	{
		//CategoryNode.children.put(key, child);
	   	//children[child->name] = child;
	}

}
