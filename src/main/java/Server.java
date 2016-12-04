import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.HashMap;
import java.math.BigInteger;

public class Server extends Thread {
    
    private static final int serverPort = 7333;
    private boolean listening = true;
    private String username;
    private Encryption e;
    private ServerSocket sock;
    private HashMap<String, Connection> connections = new HashMap<String, Connection>();
    private Connection selectedConnection;

    public Server(String username, Encryption e) throws IOException {
        this.username = username; 
        this.e = e;
        sock = new ServerSocket(serverPort);
    }

    public void run() {
        while (listening) {
            try {
                // create socket
                Socket client = sock.accept();
                //BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                DataInputStream in = new DataInputStream(client.getInputStream());
                DataOutputStream out = new DataOutputStream(client.getOutputStream());

                // get username
                int usernameLength = in.readInt();
                String otherUsername = "err";
                if (usernameLength > 0) {
                    byte[] otherUsernameBytes = new byte[usernameLength];
                    in.readFully(otherUsernameBytes, 0, otherUsernameBytes.length);
                    otherUsername = new String(otherUsernameBytes);
                }
                // Send username
                byte[] usernameBytes = username.getBytes("utf-8");
                out.writeInt(usernameBytes.length);
                out.write(usernameBytes);
                
                // Get public key from connection
                int keyLength = in.readInt();
                byte[] otherPub = null;
                if (keyLength > 0) {
                    otherPub = new byte[keyLength];
                    in.readFully(otherPub, 0, otherPub.length);
                }
                
                // send public key
                byte[] pub = e.getPublicKey().toByteArray();
                out.writeInt(pub.length);
                out.write(pub);

                Application.log.addLine("* connection from " + otherUsername);

                // create connection and store for use
                Connection c = new Connection(otherUsername, new BigInteger(otherPub), client, e);
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
            //BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            DataInputStream in = new DataInputStream(client.getInputStream());
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            // initiator sends username first
            byte[] usernameBytes = username.getBytes("utf-8");
            out.writeInt(usernameBytes.length);
            out.write(usernameBytes);
            // get their username
            int usernameLength = in.readInt();
            String otherUsername = "err";
            if (usernameLength > 0) {
                byte[] otherUsernameBytes = new byte[usernameLength];
                in.readFully(otherUsernameBytes, 0, otherUsernameBytes.length);
                otherUsername = new String(otherUsernameBytes);
            }

            // initiator sends public key first
            byte[] pub = e.getPublicKey().toByteArray();
            out.writeInt(pub.length);
            out.write(pub);
            // get their public key
            int keyLength = in.readInt();
            byte[] otherPub = null;
            if (keyLength > 0) {
                otherPub = new byte[keyLength];
                in.readFully(otherPub, 0, otherPub.length);
            }

            Application.log.addLine("* connected to " + otherUsername);

            // create and store connection
            Connection c = new Connection(otherUsername, new BigInteger(otherPub), client, e);
            selectedConnection = c;
            connections.put(username, c);
            c.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void setSelectedConnection(Connection c) {
        this.selectedConnection = c;
    }

}
