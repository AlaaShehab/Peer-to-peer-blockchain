import java.util.ArrayList;
import java.util.List;

public class Log {
	
	private List <Block> preprepareRequests = new ArrayList<>();
	private List <Block> prepareRequests = new ArrayList<>();
	private List <Block> commitRequests = new ArrayList<>();

	public int getPrepareRequestsCount(Block b){
		int count=0;
		for (int i = 0; i < prepareRequests.size(); i++) {
			if(prepareRequests.get(i).getHash().equals(b.getHash())){
				count++;
			}
		}
		return count;
	}
	public int getCommitRequestsCount(Block b){
		int count=0;
		for (int i = 0; i < commitRequests.size(); i++) {
			if(commitRequests.get(i).getHash().equals(b.getHash())){
				count++;
			}
		}
		return count;
	}
	public void addPrepareRequest(Block b){
		prepareRequests.add(b);
	}
	public void addCommitRequest(Block b){
		prepareRequests.add(b);
	}
}
