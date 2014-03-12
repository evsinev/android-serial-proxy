package com.serial_proxy;

import java.io.IOError;
import java.util.concurrent.atomic.AtomicBoolean;

public class SerialProxyServerTask implements Runnable {

    private static final Logger LOG = Logger.create(SerialProxyServerTask.class);

    @Override
    public void run() {
        try {

            NetworkManager network = new NetworkManager();
            BluetoothManager serial = new BluetoothManager();

            while (!Thread.currentThread().isInterrupted()) {
                network.acceptConnection();

                try {

                    serial.connectToDevice();

                    StreamPipe networkSerial = new StreamPipe("net-bt", network.getInputStream(), serial.getOutputStream());
                    StreamPipe serialNetwork = new StreamPipe("bt-net", serial.getInputStream(), network.getOutputStream());
                    while(network.isConnected() && serial.isConnected()) {
                        try {
                            networkSerial.writeIfAvailable();
                            serialNetwork.writeIfAvailable();
                        } catch (Exception e) {
                            LOG.error("Communication failure", e);
                            break;
                        }

//                        LOG.debug("Waiting for data. Sleep(1000)...");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }

                } catch (Exception e) {
                    LOG.error("Can't connect to device", e);
                } finally {
                    serial.close();
                    network.close();
                }

            }

        } catch (Exception e) {
            LOG.error("Main cycle error: "+e.getMessage(), e);
        }
    }

}
