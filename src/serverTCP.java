
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

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
 
        public ClientHandler(Socket socket, ArrayList<String> txList,ArrayList<String> blockList) {
            this.clientSocket = socket;
            this.txList=txList;
            this.blockList =blockList;
        }
        private boolean isReceivedStringBlock (String received) {
            Gson parser = new Gson();
            Block block = parser.fromJson(received, Block.class);
            return !block.hash().isEmpty();
        }

        private boolean isReceivedStringTransaction (String received) {
            Gson parser = new Gson();
            Transaction transaction = parser.fromJson(received, Transaction.class);
            return !transaction.getId().isEmpty();
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
				    if(isReceivedStringTransaction(inputLine)) {
				    	System.out.println("Rita : add transaction "+inputLine);
				    	txList.add(inputLine);
				    }else if(isReceivedStringBlock(inputLine)){
				    	System.out.println("Rita : add block "+inputLine);
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
