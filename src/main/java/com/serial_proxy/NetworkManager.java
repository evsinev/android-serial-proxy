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

    private final int port;
    public NetworkManager(int aPort)  {
        port = aPort;
    }

    public void open() throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void acceptConnection() throws IOException {

        LOG.debug("Listening on %s:%d ...", getIPAddress(true), serverSocket.getLocalPort());
        socket = serverSocket.accept();
        socket.setSoTimeout(60000);
        LOG.debug("Accepted: %s", socket.toString());

    }

    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4  true=return ipv4, false=return ipv6
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
    private ServerSocket serverSocket;

    public void close() {
        LOG.debug("Closing connection");
        try {
            socket.getOutputStream().flush();
            socket.getOutputStream().close();
//            socket.getInputStream().close();
//            socket.shutdownOutput();
//            socket.shutdownInput();
            socket.close();
        } catch (IOException e) {
            LOG.error("error closing socket", e);
        }
    }

    public void stopListening() {
        if(serverSocket==null) {
            LOG.warn("Server socket in not bound");
        } else {
            try {
                serverSocket.close();
            } catch (Exception e) {
                LOG.error("Error closing server socket", e);
            }
        }
    }
}
