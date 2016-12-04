import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.math.BigInteger;

public class Connection extends Thread {
    private String name;
    private BigInteger pubKey;
    private Socket socket;
    private Encryption e;

    public Connection(String name, BigInteger pubKey, Socket socket, Encryption e) {
        this.name = name;
        this.pubKey = pubKey;
        this.socket = socket;
        this.e = e;
    }

    public void run() {
        try {
            //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataInputStream in = new DataInputStream(socket.getInputStream());
            int msgLength = in.readInt();
            while (msgLength > 0) {
                byte[] ciphertext = new byte[msgLength];
                in.readFully(ciphertext, 0, ciphertext.length);
                String msg = e.decrypt(ciphertext);
                Application.log.addLine("[" + name + "] " + msg);
                msgLength = in.readInt();
            }    
            Application.log.addLine("* " + name + " disconnected");
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean sendMessage(String msg) {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            byte[] ciphertext = Encryption.encrypt(pubKey, msg);
            out.writeInt(ciphertext.length);
            out.write(ciphertext);
            Application.log.addLine("[you] " + msg);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void close() throws IOException {
        socket.close();
    }

    public boolean isClosed() {
        return socket.isClosed();
    }
}
