import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.BrokenBarrierException;

public class Main {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws InterruptedException, BrokenBarrierException, IOException {
		
		Hashtable<Integer, Integer> PortsTable = new Hashtable<Integer, Integer>();
		
		PeerNode peer1 = new PeerNode(5000, "127.0.0.1", 1);
		PortsTable.put(1, 5000);
		PeerNode peer2 = new PeerNode(5001, "127.0.0.1", 2);
		PortsTable.put(2, 5001);
		PeerNode peer3 = new PeerNode(5002, "127.0.0.1", 3);
		PortsTable.put(3, 5002);
		
		Hashtable<Integer, Integer> h1 = (Hashtable<Integer, Integer>)PortsTable.clone(); 
		h1.remove(1);
		peer1.broadcast(h1,"txdataset.txt");
	    ArrayList<String> l = peer2.txList;
	    for(int i=0;i<l.size();i++)
	    	System.out.println(l.get(i));
	    

	}

}
