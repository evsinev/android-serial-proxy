package com.serial_proxy.bluetooth;

import android.os.Build;

public class BluetoothManagerFactory {

    public static IBluetoothManager create() {
        return isEmulator() ? new BluetoothManagerEmulator() : new BluetoothManager(null);
    }

    private static boolean isEmulator() {
        return "sdk_x86".equals(Build.PRODUCT);
    }
}
