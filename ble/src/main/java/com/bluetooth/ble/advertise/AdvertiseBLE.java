package com.bluetooth.ble.advertise;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;

import com.bluetooth.ble.discover.DiscoverBLE;

import java.util.UUID;

public class AdvertiseBLE {
    private static final AdvertiseBLE ourInstance = new AdvertiseBLE();

    public static AdvertiseBLE getInstance() {
        return ourInstance;
    }

    private Context context;
    private AdvertiseListener advertiseListener;
    private AdvertiseCallback advertiseCallback;
    private BluetoothLeAdvertiser advertiser;
    private boolean isStart;

    private AdvertiseBLE() {
    }

    public void setAdvertiseListener(AdvertiseListener advertiseListener) {
        this.advertiseListener = advertiseListener;
    }

    private void checkDiscoverBLE() {
        if (DiscoverBLE.getInstance().isScanner()) {
            DiscoverBLE.getInstance().stopScan();
        }
    }

    public void startAdversite(String dataBroadcast, String serviceUUID) {
        checkDiscoverBLE();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            advertiseCallback = new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    super.onStartSuccess(settingsInEffect);

                    isStart = true;
                    advertiseListener.onAdvertiseSuccess();
                }

                @Override
                public void onStartFailure(int errorCode) {
                    super.onStartFailure(errorCode);

                    isStart = false;
                    advertiseListener.onAdvertiseFail(errorCode);
                }
            };

            BluetoothAdapter.getDefaultAdapter().setName(dataBroadcast);

            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                    .setConnectable(true)
                    .build();

            ParcelUuid uuid = new ParcelUuid(UUID.fromString(serviceUUID));

            AdvertiseData data = new AdvertiseData.Builder()
                    .addServiceUuid(uuid)
                    .setIncludeDeviceName(true)
                    .build();

            advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
            advertiser.startAdvertising(settings, data, advertiseCallback);
        }
    }

    public void stopAdvertise() {
        isStart = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            advertiser.stopAdvertising(advertiseCallback);
        }
    }

    public boolean isAdvertising() {
        return isStart;
    }
}
