
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class serverTCP {
	private ServerSocket serverSocket;
	private ClientHandler clientHandler;

	public void start(int port, ArrayList<String> txList, ArrayList<String> blockList) {
    	try {
	    	serverSocket = new ServerSocket(port);
	    	
			while (true) {
				clientHandler= new ClientHandler(serverSocket.accept(),txList,blockList);
						clientHandler.start();
			}
	         
			} catch (IOException e) {
				e.printStackTrace();
			}
    }
 
    public void stop() throws IOException {
        serverSocket.close();
    }
 
    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private ArrayList<String> txList;
        private ArrayList<String> blockList;
        static boolean isTx=false;
 
        public ClientHandler(Socket socket, ArrayList<String> txList,ArrayList<String> blockList) {
            this.clientSocket = socket;
            this.txList=txList;
            this.blockList =blockList;
        }
 
        public void run() {
        	
        	try {
	            out = new PrintWriter(clientSocket.getOutputStream(), true);
	            in = new BufferedReader(
	              new InputStreamReader(clientSocket.getInputStream()));
	             
	            String inputLine;            
				while ((inputLine = in.readLine()) != null) {
					TimeUnit.SECONDS.sleep(1);
					//TODO remove it or state instruction for the end
				    if (".".equals(inputLine)) {
				        out.println("bye");
				        break;
				    }
				    if ("transaction".equals(inputLine)) {
				        isTx=true;
				    }
				    if ("block".equals(inputLine)) {
				        isTx=false;
				    }
				    if(isTx) {
				    	txList.add(inputLine);
				    }else {
				    	blockList.add(inputLine);
				    }
				    out.println(inputLine);
				    
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	 try {
	            in.close();
	            out.close();
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
}
