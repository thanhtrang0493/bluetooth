package com.bluetooth.ble.connect;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;

import com.bluetooth.ble.utils.BluetoothUtils;

import java.util.UUID;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothConnectCallback extends BluetoothGattCallback {

    private BluetoothConnectListener listener;
    private BluetoothGattCharacteristic writeCharacteristic;
    private Context context;
    private BluetoothGattCharacteristic notifyCharacteristic;

    public void setBluetoothConnectListener(BluetoothConnectListener listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            gatt.discoverServices();
        } else {
            connectFail(status, newState);
        }
    }

    private void connectFail(final int status, final int newState) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onConnectDeviceFail(status, newState);
                }
            }
        });
    }

    private void connectSuccess(final BluetoothGattCharacteristic characteristic) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onConnectDeviceSuccess(characteristic);
                }
            }
        });
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        if (status != BluetoothGatt.GATT_SUCCESS) {
            connectFail(status, -1);
            return;
        }

        if (gatt != null) {
            for (BluetoothGattService service : gatt.getServices()) {
                if (service.getUuid().toString().equals(BluetoothUtils.SERVICE_UUID)) {

                    //get characteristic write and notification
                    writeCharacteristic = service.getCharacteristic(UUID.fromString(BluetoothUtils.WRITE_CHARACTERISTIC_UUID));
                    notifyCharacteristic = service.getCharacteristic(UUID.fromString(BluetoothUtils.NOTIFY_CHARACTERISTIC_UUID));

                    enableCharacteristicNotification(gatt);

                    connectSuccess(writeCharacteristic);

                    return;
                }
            }
        }
    }

    private void enableCharacteristicNotification(BluetoothGatt gatt) {
        gatt.setCharacteristicNotification(notifyCharacteristic, true);
        BluetoothGattDescriptor notifyDescriptor = notifyCharacteristic.getDescriptor(UUID.fromString(BluetoothUtils.NOTIFY_DESCRIPTOR_UUID));

        if (notifyDescriptor != null) {
            notifyDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(notifyDescriptor);
        }
    }

    //the method will be called when characteristic written successfully / failed
    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            writeSuccess(characteristic);
        } else {
            writeFail(status);
        }
    }

    private void writeSuccess(final BluetoothGattCharacteristic characteristic) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onWriteCommandSuccess(characteristic);
                }
            }
        });
    }

    private void writeFail(final int status) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onWriteCommandFail(status);
                }
            }
        });
    }

    //when the characteristic is read, the onCharacteristicRead method will be called,
    // you can receive here the characteristic value and update the UI
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            readSuccess(characteristic.getValue());
        } else {
            readFail(status);
        }
    }

    private void readSuccess(final byte[] data) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onReadCommandSuccess(data);
                }
            }
        });
    }

    private void readFail(final int status) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onReadCommandFail(status);
                }
            }
        });
    }

    //when the characteristic value automatically changes
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        readSuccess(characteristic.getValue());
    }
}
