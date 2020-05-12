import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class PeerNode {
	int port;
	String hostName;
	int ID;
	clientTCP client1;
	serverTCP server1;
	ArrayList<String> txList;
	
	public PeerNode(int port,String hostName,int ID) throws InterruptedException, BrokenBarrierException {
		this.hostName=hostName;
		this.port =port;
		this.ID = ID;
		server1 = new serverTCP();
		client1 = new clientTCP();
		txList = new ArrayList<String>();
		final CyclicBarrier gate = new CyclicBarrier(2);
		Thread t1 = new Thread(){
    	    public void run(){
    	    	try {
    	        	gate.await();
    	        	server1.start(port,txList);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	    }};

	        t1.start();
	        gate.await();
	}
	public void broadcast(Hashtable<Integer, Integer> toPortsTable, String txFileName) throws IOException{
		Enumeration<Integer> e = toPortsTable.elements();
        while (e.hasMoreElements()) { 
        	client1.startConnection("127.0.0.1", e.nextElement());
        	File file=new File(txFileName);    //creates a new file instance  
        	FileReader fr=new FileReader(file);   //reads the file  
        	BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream   //constructs a string buffer with no characters  
        	String line;  
        	while((line=br.readLine())!=null)  
        	{  
        		String msg1 = client1.sendMessage(line);
        	    //System.out.println(msg1); 
        	}  
        	fr.close();    	    
        } 
    	
       client1.stopConnection();
	}
	
}
