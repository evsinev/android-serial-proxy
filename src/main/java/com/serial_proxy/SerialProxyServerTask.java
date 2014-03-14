package com.serial_proxy;

import com.serial_proxy.bluetooth.BluetoothManager;
import com.serial_proxy.settings.BindingProfile;

import java.util.concurrent.atomic.AtomicInteger;

public class SerialProxyServerTask extends Thread  {

    private static final Logger LOG = Logger.create(SerialProxyServerTask.class);

    private static final AtomicInteger THREAD_INDEX = new AtomicInteger(0);

    private final BindingProfile profile;
    private final NetworkManager network;
    private final BluetoothManager serial;

    public SerialProxyServerTask(BindingProfile aProfile) {
        profile = aProfile;
        network = new NetworkManager(profile.port);
        serial = new BluetoothManager(profile.bluetoothAddress);
    }

    @Override
    public void run() {
        try {

            setName(profile.title+"-"+THREAD_INDEX.incrementAndGet());

            network.open();
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

                        LOG.debug("Waiting for data. Sleep(1000)...");
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

    public void cancel() {
        network.stopListening();
        interrupt();
    }

}
