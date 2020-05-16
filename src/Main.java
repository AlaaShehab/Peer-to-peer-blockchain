import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;

public class Main {

	public static void main(String[] args) throws InterruptedException, BrokenBarrierException, IOException {
		
		Client client1 = new Client(1024, "127.0.0.1", 1);
		Miner miner1 = new Miner(1025, "127.0.0.1", 2);

		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					System.out.println("Main - transaction - Interrupted");
				}
				miner1.receiveTransaction();
			}
		}).start();

		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					System.out.println("Main - Block - Interrupted");
				}
				miner1.receiveBlock();
			}
		}).start();

		miner1.restartMiningThread();

//		miner1.restartMiningThread();

		Miner miner2 = new Miner(1026, "127.0.0.1", 3);
		
		//Test client Broadcast
		client1.readTransaction("/home/rita/git/Peer-to-peer-blockchai/src/txdataset");
		
		
		System.out.println("miner1");
		synchronized(miner1.txList) 
        { 
            // must be in synchronized block 
            Iterator it = miner1.txList.iterator(); 
  
            while (it.hasNext()) 
                System.out.println(it.next()); 
        } 
//	    System.out.println("miner2");
//	    ArrayList<String> l2 = miner2.txList;
//	    for(int i=0;i<l2.size();i++)
//	    	System.out.println(l2.get(i));
	    

	}

}
