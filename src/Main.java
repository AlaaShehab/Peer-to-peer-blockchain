import java.io.IOException;
import java.util.ArrayList;
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
					System.out.println("Main - transaction - error sleeping");
				}
				miner1.receiveTransaction();
			}
		}).start();

		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					System.out.println("Main - transaction - error sleeping");
				}
				miner1.mineBlock();
			}
		}).start();

		Miner miner2 = new Miner(1026, "127.0.0.1", 3);
		
		//Test client Broadcast
		client1.readTransaction("txdataset");
		
		
		System.out.println("miner1");
		ArrayList<String> l = miner1.txList;
	    for(int i=0;i<l.size();i++)
	    	System.out.println(l.get(i));
	    System.out.println("miner2");
	    ArrayList<String> l2 = miner2.txList;
	    for(int i=0;i<l2.size();i++)
	    	System.out.println(l2.get(i));
	    

	}

}
