package com.bluetooth.ble.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.os.Build;

public class BluetoothConnectService {
    private static final BluetoothConnectService ourInstance = new BluetoothConnectService();

    public static BluetoothConnectService getInstance() {
        return ourInstance;
    }

    private BluetoothGatt bluetoothGatt;
    private BluetoothConnectCallback bluetoothConnectCallback;

    private BluetoothConnectService() {
        bluetoothConnectCallback = new BluetoothConnectCallback();
    }

    public boolean connectDevice(Context context, BluetoothDevice bluetoothDevice, BluetoothConnectListener listener) {

        //set listener connect
        bluetoothConnectCallback.setBluetoothConnectListener(listener, context);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //BluetoothAdapter not initialized or unspecified address.
        if (bluetoothAdapter == null || bluetoothDevice.getAddress() == null) {
            return false;
        }
        // Previously connected device. Try to reconnect.
        if (bluetoothGatt != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (bluetoothGatt.connect()) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        //Device not found.  Unable to connect.
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(bluetoothDevice.getAddress());
        if (device == null) {
            return false;
        }

        // We want to directly connect to the device, so we are setting the
        // autoConnect
        // parameter to false.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bluetoothGatt = device.connectGatt(context, false, bluetoothConnectCallback);
        }
        return true;
    }

    public void disconnectDevice() {
        if (bluetoothGatt != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bluetoothGatt.disconnect();
                try {
                    Thread.sleep(600);
                    bluetoothGatt.close();
                    bluetoothGatt = null;
                } catch (InterruptedException e) {

                }
            }
        }
    }

    public boolean writeCommand(byte[] command, BluetoothGattCharacteristic characteristic) {
        boolean isSuccess = false;
        if (bluetoothGatt != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (characteristic != null) {
                    characteristic.setValue(command);
                    isSuccess = bluetoothGatt.writeCharacteristic(characteristic);
                }
            }
        }
        return isSuccess;
    }
}
