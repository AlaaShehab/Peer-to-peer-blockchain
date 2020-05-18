import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeUnit;

public class Main {

	public static void main(String[] args) throws InterruptedException, BrokenBarrierException, IOException {
		
		Client client1 = new Client(1024, "127.0.0.1", 1);
		Client client2 = new Client(1025, "127.0.0.1", 2);
		Client client3 = new Client(1026, "127.0.0.1", 3);
		Miner miner1 = new Miner(1027, "127.0.0.1", 4);

		new Thread(() -> {
			while (true) {
				try {
                    TimeUnit.MICROSECONDS.sleep(1);
				} catch (InterruptedException e) {
					System.out.println("Main - transaction - Interrupted");
				}
				miner1.receiveTransaction();
			}
		}).start();

		new Thread(() -> {
			while (true) {
				try {
                    TimeUnit.MICROSECONDS.sleep(1);
				} catch (InterruptedException e) {
					System.out.println("Main - Block - Interrupted");
				}
				miner1.receiveBlock();
			}
		}).start();

		miner1.restartMiningThread();

		Miner miner2 = new Miner(1028, "127.0.0.1", 5);

		new Thread(() -> {
			while (true) {
				try {
                    TimeUnit.MICROSECONDS.sleep(1);
				} catch (InterruptedException e) {
					System.out.println("Main - transaction - Interrupted");
				}
				miner2.receiveTransaction();
			}
		}).start();

		new Thread(() -> {
			while (true) {
				try {
                    TimeUnit.MICROSECONDS.sleep(1);
				} catch (InterruptedException e) {
					System.out.println("Main - Block - Interrupted");
				}
				miner2.receiveBlock();
			}
		}).start();
		

		miner2.restartMiningThread();
		
		Miner miner3 = new Miner(1029, "127.0.0.1", 6);

		new Thread(() -> {
			while (true) {
				try {
                    TimeUnit.MICROSECONDS.sleep(1);
				} catch (InterruptedException e) {
					System.out.println("Main - transaction - Interrupted");
				}
				miner3.receiveTransaction();
			}
		}).start();

		new Thread(() -> {
			while (true) {
				try {
                    TimeUnit.MICROSECONDS.sleep(1);
				} catch (InterruptedException e) {
					System.out.println("Main - Block - Interrupted");
				}
				miner3.receiveBlock();
			}
		}).start();
		

		miner3.restartMiningThread();
		
		Miner miner4 = new Miner(1030, "127.0.0.1", 7);

		new Thread(() -> {
			while (true) {
				try {
                    TimeUnit.MICROSECONDS.sleep(1);
				} catch (InterruptedException e) {
					System.out.println("Main - transaction - Interrupted");
				}
				miner4.receiveTransaction();
			}
		}).start();

		new Thread(() -> {
			while (true) {
				try {
                    TimeUnit.MICROSECONDS.sleep(1);
				} catch (InterruptedException e) {
					System.out.println("Main - Block - Interrupted");
				}
				miner4.receiveBlock();
			}
		}).start();
		

		miner4.restartMiningThread();
		
		//Test client Broadcast
		client1.readTransaction("/home/rita/git/Peer-to-peer-blockchai/src/txdataset");
		TimeUnit.SECONDS.sleep(2);
		System.out.println("messages");
		List<Integer> l = miner1.messagesCountList;
		int sum=0;
		for(int i=0;i<l.size();i++) {
	    	System.out.print(l.get(i)/7+" ");
	    	sum+=(l.get(i)/7);
		}
		System.out.println();
		System.out.println("average = "+sum/l.size());
		System.out.println("stale blocks");
		System.out.println(miner1.chain.numberOfStaleBlocks ());
		sum=0;
		System.out.println("times");
		ArrayList<Long> m =miner1.chain.block.miningTimeList;
		for(int i=0;i<m.size();i++) {
	    	System.out.print(m.get(i)+" ");
	    	sum+=(m.get(i));
		}
		System.out.println();
		System.out.println("average = "+sum/m.size());
		
		
		
		
		
//		System.out.println("miner1");
//		List<String> l = miner1.txList;
//	    for(int i=0;i<l.size();i++)
//	    	System.out.println(l.get(i));
//	    System.out.println("miner2");
//	    ArrayList<String> l2 = miner2.txList;
//	    for(int i=0;i<l2.size();i++)
//	    	System.out.println(l2.get(i));
	    

	}

}
