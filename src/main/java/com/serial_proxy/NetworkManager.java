package com.serial_proxy;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;

public class NetworkManager implements ISocket {

    private static final Logger LOG = Logger.create(NetworkManager.class);

    public NetworkManager() throws IOException {
         serverSocket = new ServerSocket(1234);
    }

    public void acceptConnection() throws IOException {

         LOG.debug("Accepting connection on %s:%d ...", getIPAddress(true),serverSocket.getLocalPort());
         socket = serverSocket.accept();
        LOG.debug("Accepted: %s", socket.toString());

    }

    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    public boolean isConnected() {
        boolean connected = socket.isConnected();
        if(!connected) {
            LOG.debug("Not connected");
        }
        return connected;
    }

    /**
     * Get IP address from first non-localhost interface
     * @param ipv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim<0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }

    private Socket socket;
    private final ServerSocket serverSocket;

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            LOG.error("error closing socket", e);
        }
    }
}
