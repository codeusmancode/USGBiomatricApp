/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usgbiomatricsapp.customs;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author usmanriaz
 */
public class NetworkStatus extends Thread {

    private static int THREAD_SLEEP_TIME = 10000;
    private static int SOCKET_TIMEOUT = 5000;
    public static String NETWORK_ACTION = "network_action";
    private Socket socket;
    private static String HOST = "usgtms";//prod
    // private static String HOST = "usgapps";//test
    private static int LISTEN_PORT = 1521;
    private ActionListener listener;

    public class NetworkEvent extends ActionEvent {

        public boolean networkConnected;
        public boolean serverRunning;

        public NetworkEvent(Object source, String action, boolean network, boolean server) {
            super(source, ActionEvent.ACTION_PERFORMED, action);
            this.networkConnected = network;
            this.serverRunning = server;
        }
    }

    public NetworkStatus(ActionListener l) {
        this.listener = l;

        start();
    }

    private void notifyListener(boolean server, boolean network) {
        NetworkEvent e = new NetworkEvent(this, NETWORK_ACTION, network, server);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                NetworkStatus.this.listener.actionPerformed(e);
            }
        });
    }

    @Override
    public void run() {
        while (true) {
            try {
                socket = new Socket();
                SocketAddress socketAddress = new InetSocketAddress(HOST, LISTEN_PORT);
                socket.connect(socketAddress, SOCKET_TIMEOUT);
                
                notifyListener(true, true);//notifyListener(server,network)
            } catch (SocketTimeoutException ste) {
                
                System.err.println("ste "+ste.getMessage());
                notifyListener(false, true);
            } catch (UnknownHostException uhe) {
                System.err.println("uhe "+uhe.getMessage());
                notifyListener(true, false);
            } catch (IOException ioe) {
                System.err.println("ioe "+ioe.getMessage());
                notifyListener(false, true);
            } finally {
                try {
                    socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(NetworkStatus.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            try {
                //sleep for 3 seconds
                Thread.sleep(THREAD_SLEEP_TIME);
            } catch (InterruptedException ex) {
                Logger.getLogger(NetworkStatus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
