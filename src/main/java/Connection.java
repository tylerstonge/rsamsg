import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection extends Thread {
    private String name;
    private Socket socket;

    public Connection(String name, Socket socket) {
        this.name = name;
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String msg = in.readLine();
            while (msg != null) {
                System.out.println("[" + name + "] " + msg);
                msg = in.readLine();
            }    
            System.out.println("* " + name + " disconnected");
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeBytes(msg + "\n");
            System.out.println("[you] " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        socket.close();
    }
}
