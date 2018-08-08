package com.bluetooth.ble.connect;

import android.bluetooth.BluetoothGattCharacteristic;

public interface BluetoothConnectListener {
    void onConnectDeviceSuccess(BluetoothGattCharacteristic characteristic);

    void onConnectDeviceFail(int status, int newState);

    void onWriteCommandSuccess(BluetoothGattCharacteristic characteristic);

    void onWriteCommandFail(int status);

    void onReadCommandSuccess(byte[] data);

    void onReadCommandFail(int status);
}
