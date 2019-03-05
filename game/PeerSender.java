package game;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayDeque;
import java.util.Queue;

public class PeerSender extends Thread {

    public Socket connection;
    private String ip;
    private Queue<String> cmdQueue = new ArrayDeque();

    public PeerSender(String ip) {
        this.ip = ip;
    }

    private void connect() {
        try {
            if (!isConnected()) {
                System.out.println("Calling " + ip + "...");
                connection = new Socket();
                connection.connect(new InetSocketAddress(ip, 6666), 2000);
                System.out.println("Call connected with " + ip + "!");
                Main.giveConnection(connection);
            } else {
                return;
            }
        } catch (SocketTimeoutException e) {}
        catch (IOException e) {
            //System.out.println(String.format("PeerSender[%s]::%s::%s", ip, e.getClass(), e.getMessage()));
            e.printStackTrace();
        }

        connect();
    }

    private void send() {
        try {
            DataOutputStream output = new DataOutputStream(connection.getOutputStream());
            while(true) {
                if (!cmdQueue.isEmpty()) {
                    output.writeBytes(cmdQueue.poll());
                }
            }
        } catch (IOException e) {
            System.out.println(String.format("PeerSender[%s]::%s::%s", ip, e.getClass(), e.getMessage()));
        }
    }

    @Override
    public void run() {
        connect();
        send();
    }

    public void queueCommand(Command cmd) {
        cmdQueue.add(cmd.toString() + "\n");
    }

    public boolean isConnected() {
        return (connection != null) && connection.isConnected();
    }

    public void giveConnection(Socket connection) {
        this.connection = connection;
    }

    public String getIP() {
        return ip;
    }

}
