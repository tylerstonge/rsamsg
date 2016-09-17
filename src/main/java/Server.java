import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.HashMap;

public class Server extends Thread {
    
    private static final int serverPort = 7333;
    private boolean listening = true;
    private String username;
    private ServerSocket sock;
    private HashMap<String, Connection> connections = new HashMap<String, Connection>();
    private Connection selectedConnection;

    public Server(String username) throws IOException {
        this.username = username; 
        sock = new ServerSocket(serverPort);
    }

    public void run() {
        while (listening) {
            try {
                // create socket
                Socket client = sock.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                DataOutputStream out = new DataOutputStream(client.getOutputStream());

                // get username and alter user of new connection
                String otherUsername = in.readLine();
                out.writeBytes(username + "\n");

                System.out.println("* connection from " + otherUsername);

                // create connection and store for use
                Connection c = new Connection(otherUsername, client);
                selectedConnection = c;
                connections.put(username, c);
                c.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void connect(String ip) {
        try {
            Socket client = new Socket(ip, serverPort);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            // initiator sends username first
            out.writeBytes(username + "\n");
            // get their username
            String otherUsername = in.readLine();

            System.out.println("* connected to " + otherUsername);

            // create and store connection
            Connection c = new Connection(otherUsername, client);
            selectedConnection = c;
            connections.put(username, c);
            c.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isListening() {
        return listening;
    }

    public void setListening(boolean listening) {
        this.listening = listening;
    }

    public Connection getSelectedConnection() {
        return this.selectedConnection;
    }

}
