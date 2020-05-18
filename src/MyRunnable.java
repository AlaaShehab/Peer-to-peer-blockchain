
public class MyRunnable implements Runnable {
	Block block ;
	Miner miner;
	int flag;
	   public MyRunnable(Block b, Miner m,int flag) {
		   block=b;
		   miner=m;
		   this.flag=flag;
	   }

	   

	public void run() {
		switch(flag){
		case 0:
			 while(miner.getBftLog().getPrepareRequestsCount(block)<3);
			   block.setState(Utils.states.commit);
	       	   miner.getBftLog().addCommitRequest(block);
	       	   miner.broadcastBlock(block);
	       	   break;
		case 1:
			 while(miner.getBftLog().getCommitRequestsCount(block)<3);
			  miner.chain.addBlock(block);
	       	   break;
		}
		  

		   
	   }
	}