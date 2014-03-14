package com.serial_proxy.bluetooth;

import java.io.Serializable;

public interface IBluetoothManager {


    public IBluetoothManager.DeviceInfo[] getDevices();

    class DeviceInfo implements Serializable {
        public final String name;
        public final String address;

        public DeviceInfo(String name, String address) {
            this.name = name;
            this.address = address;
        }

        @Override
        public String toString() {
            return name + " " + address;
        }
    }
}
