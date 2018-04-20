package com.bluetooth.ble.broadcast;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverBluetoothState extends BroadcastReceiver {

    private BluetoothListener bluetoothListener;

    public void setBluetoothListener(BluetoothListener bluetoothListener) {
        this.bluetoothListener = bluetoothListener;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        switch (action) {
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                bluetoothStateChange(intent);
                break;
        }
    }

    private void bluetoothStateChange(Intent intent) {
        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
                if (bluetoothListener != null)
                    bluetoothListener.bluetoothOff();
                break;
            case BluetoothAdapter.STATE_ON:
                if (bluetoothListener != null)
                    bluetoothListener.bluetoothOn();
                break;
        }
    }
}
