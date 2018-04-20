package com.bluetooth.ble.discover;

import android.bluetooth.BluetoothDevice;

public interface DiscoverListener {
    void onDiscoverScanResult(BluetoothDevice bluetoothDevice);

    void onScanFail(int errorCode);

    void onDiscoverStopScan();
}
