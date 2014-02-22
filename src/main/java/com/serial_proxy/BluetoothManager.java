package com.serial_proxy;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothManager implements ISocket {

    public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    private static final Logger LOG = Logger.create(BluetoothManager.class);

    public BluetoothManager() {
       bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
       if(bluetoothAdapter==null) {
           throw new IllegalStateException("Bluetooth is down. Please enable bluetooth");
       }

    }

    public void connectToDevice() throws IOException {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice("20:13:03:03:27:6F");

        LOG.debug("Device is %s - %s", device.getName(), device.getAddress());

        LOG.debug("Creating BT socket...");
        socket = device.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));

        if(!bluetoothAdapter.cancelDiscovery()) {
            LOG.warn("Can't cancel discovery");
        }

        LOG.debug("Connecting to device...");
        socket.connect();
        LOG.debug("Connected");
    }

    public boolean isConnected() {
        boolean connected = socket.isConnected();
        if(!connected) {
            LOG.debug("Not connected");
        }
        return connected;
    }

    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            LOG.error("error closing socket", e);
        }
    }

    private BluetoothSocket socket;
    BluetoothAdapter bluetoothAdapter;
}
