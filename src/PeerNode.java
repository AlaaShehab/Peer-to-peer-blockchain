import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class PeerNode {
	int port;
	String hostName;
	int ID;
	clientTCP client1;
	serverTCP server1;
	List<String> txList;
	List<String> blockList;
	//TODO first load it from file, and last save it to the file
	static String portsFile = "ports.txt";
	static Hashtable<Integer, Integer> minersPorts;
	static Hashtable<Integer, Integer> clientsPorts;
    static int messagesCount=0;
    static public List<Integer> messagesCountList;
	
	public PeerNode(int port,String hostName,int ID,String type) throws InterruptedException, BrokenBarrierException {
		this.hostName=hostName;
		this.port =port;
		this.ID = ID;
		messagesCountList = new ArrayList<Integer>();
		server1 = new serverTCP();
		client1 = new clientTCP();
		txList = Collections.synchronizedList(new ArrayList<String>());
		blockList = Collections.synchronizedList(new ArrayList<String>());
		if(type.equals("miner")) {
			if(minersPorts==null) {
				minersPorts = new Hashtable<Integer, Integer>();
			}
			minersPorts.put(ID, port);
		}else if(type.equals("client")) {
			if(clientsPorts==null) {
				clientsPorts = new Hashtable<Integer, Integer>();
			}
			clientsPorts.put(ID, port);
		}
		final CyclicBarrier gate = new CyclicBarrier(2);
		Thread t1 = new Thread(){
    	    public void run(){
    	    	try {
    	        	gate.await();
    	        	server1.start(port,txList,blockList);
				} catch (Exception e) {
					e.printStackTrace();
				}
    	    }};

	        t1.start();
	        gate.await();
	}
	/*public void broadcast(Hashtable<Integer, Integer> toPortsTable, String txFileName) throws IOException{
		Enumeration<Integer> e = toPortsTable.elements();
        
        	File file=new File(txFileName);    //creates a new file instance  
        	FileReader fr=new FileReader(file);   //reads the file  
        	BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream   //constructs a string buffer with no characters  
        	String line;  
        	while((line=br.readLine())!=null)  
        	{  
        		while (e.hasMoreElements()) { 
                	client1.startConnection("127.0.0.1", e.nextElement());
                	String msg1 = client1.sendMessage(line);            	
                	client1.stopConnection();
        		}
        		e = toPortsTable.elements();
        	}  
        	fr.close();    	    
	}*/
	
}
