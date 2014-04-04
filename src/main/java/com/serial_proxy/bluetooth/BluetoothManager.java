package com.serial_proxy.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import com.serial_proxy.ISocket;
import com.serial_proxy.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothManager implements ISocket, IBluetoothManager {

    public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    private static final Logger LOG = Logger.create(BluetoothManager.class);

    public BluetoothManager(String aAddress) {
       bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
       if(bluetoothAdapter==null) {
           throw new IllegalStateException("Bluetooth is down. Please enable bluetooth");
       }


       this.deviceAddress = aAddress!=null ? aAddress : getFirstBluetoothDevice().getAddress();

    }

    @Override
    public IBluetoothManager.DeviceInfo[] getDevices() {
        LOG.debug("Searching paired devices ...");

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        IBluetoothManager.DeviceInfo[] ret = new IBluetoothManager.DeviceInfo[bondedDevices.size()];
        int i=0;
        for (BluetoothDevice device : bondedDevices) {
            ret[i++] = new IBluetoothManager.DeviceInfo(device.getName(), device.getAddress());
        }
        return ret;
    }

    private BluetoothDevice getFirstBluetoothDevice() {
        LOG.debug("Searching paired devices ...");
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if(bondedDevices==null || bondedDevices.isEmpty()) {
            throw new IllegalStateException("No paired devices");
        }

        BluetoothDevice device = bondedDevices.iterator().next();
        LOG.debug("  First device is %s : %s", device.getName(), device.getAddress());
        return device;
    }

    public void connectToDevice() throws IOException {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);

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


    private final String deviceAddress;
    private BluetoothSocket socket;
    BluetoothAdapter bluetoothAdapter;
}
