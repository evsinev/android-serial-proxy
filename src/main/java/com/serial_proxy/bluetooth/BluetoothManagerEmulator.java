package com.serial_proxy.bluetooth;

public class BluetoothManagerEmulator implements IBluetoothManager {

    @Override
    public DeviceInfo[] getDevices() {
        return new DeviceInfo[] {new DeviceInfo("test1", "12:23:33:44:55"), new DeviceInfo("tess  2", "44:22:11:77:22")};

    }
}
