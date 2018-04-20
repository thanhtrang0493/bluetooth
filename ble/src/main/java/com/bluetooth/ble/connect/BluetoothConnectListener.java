package com.bluetooth.ble.connect;

import android.bluetooth.BluetoothGattCharacteristic;

public interface BluetoothConnectListener {
    void onConnectDeviceSuccess(BluetoothGattCharacteristic characteristic);

    void onConnectDeviceFail();

    void onWriteCommandSuccess();

    void onWriteCommandFail();

    void onReadCommandSuccess(byte[] data);

    void onReadCommandFail();
}
