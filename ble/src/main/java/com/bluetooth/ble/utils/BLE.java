package com.bluetooth.ble.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

import com.bluetooth.ble.broadcast.BluetoothListener;
import com.bluetooth.ble.broadcast.ReceiverBluetoothState;
import com.bluetooth.ble.connect.BluetoothConnectListener;
import com.bluetooth.ble.connect.BluetoothConnectService;
import com.bluetooth.ble.connect.RequestBLEEnable;
import com.bluetooth.ble.discover.DiscoverBLE;
import com.bluetooth.ble.discover.DiscoverListener;

public class BLE {

    private Context context;

    public BLE(Context context) {
        this.context = context;
    }

    /**
     * This function called onResume, add filter and listener result
     * @param bluetoothListener
     * @return
     */
    public ReceiverBluetoothState registerReceiver(BluetoothListener bluetoothListener) {
        return BluetoothConnectService.getInstance().registerReceiver(context, bluetoothListener);
    }

    public void unregisterReceiver(ReceiverBluetoothState receiverBluetoothState) {
        BluetoothConnectService.getInstance().unregisterReceiver(context, receiverBluetoothState);
    }

    public boolean checkEnableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return RequestBLEEnable.getInstance().hasPermissions(context, bluetoothAdapter);
    }

    public void startDiscover(DiscoverListener discoverListener, long TIME_OUT_DISCOVER) {
        if (checkEnableBluetooth()) {
            DiscoverBLE.getInstance().setTimeOut(TIME_OUT_DISCOVER);
            DiscoverBLE.getInstance().setDiscoverListener(discoverListener);
            DiscoverBLE.getInstance().startScan();
        }
    }

    public void stopDiscover() {
        DiscoverBLE.getInstance().stopScan();
    }

    public void connectDevice(BluetoothDevice bluetoothDevice, BluetoothConnectListener bluetoothConnectListener) {
        BluetoothConnectService.getInstance().connectDevice(context, bluetoothDevice, bluetoothConnectListener);
    }

    public void disconnectDevice() {
        BluetoothConnectService.getInstance().disconnectDevice();
    }

    public void writeCommand(byte[] command, BluetoothGattCharacteristic characteristic) {
        if (characteristic != null)
            BluetoothConnectService.getInstance().writeCommand(command, characteristic);
    }
}
