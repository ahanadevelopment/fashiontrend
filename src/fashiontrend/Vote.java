package fashiontrend;

public class Vote {
	
	int user; // ID of the user
	int item; // ID of the item
	int label; // rated or not

	long voteTime; // Unix time of the rating

	public int getUser() {
		return user;
	}

	public void setUser(int user) {
		this.user = user;
	}

	public int getItem() {
		return item;
	}

	public void setItem(int item) {
		this.item = item;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public long getVoteTime() {
		return voteTime;
	}

	public void setVoteTime(long voteTime) {
		this.voteTime = voteTime;
	}
	
	
	
}
