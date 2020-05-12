
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
    public ArrayList<String> getTxList() {
		return clientHandler.getTxList();
	}

	public void start(int port, ArrayList<String> txList) {
    	try {
	    	serverSocket = new ServerSocket(port);
	    	
			while (true) {
				clientHandler= new ClientHandler(serverSocket.accept(),txList);
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
 
        public ClientHandler(Socket socket, ArrayList<String> txList) {
            this.clientSocket = socket;
            this.txList=txList;
        }
        public ArrayList<String> getTxList() {
    		return txList;
    	}
 
        public void run() {
        	try {
	            out = new PrintWriter(clientSocket.getOutputStream(), true);
	            in = new BufferedReader(
	              new InputStreamReader(clientSocket.getInputStream()));
	             
	            String inputLine;            
				while ((inputLine = in.readLine()) != null) {
					TimeUnit.SECONDS.sleep(1);
				    if (".".equals(inputLine)) {
				        out.println("bye");
				        break;
				    }
				    out.println(inputLine);
				    txList.add(inputLine);
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
