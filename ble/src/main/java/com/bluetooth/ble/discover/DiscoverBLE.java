package com.bluetooth.ble.discover;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;

import com.bluetooth.ble.advertise.AdvertiseBLE;
import com.bluetooth.ble.utils.BluetoothUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DiscoverBLE {
    private static final DiscoverBLE ourInstance = new DiscoverBLE();

    public static DiscoverBLE getInstance() {
        return ourInstance;
    }

    private DiscoverListener discoverListener;
    private ScanCallback mCallback;
    private BluetoothLeScanner mBluetoothLeScanner;
    private boolean isStart;
    public long TIME_OUT;
    private BluetoothAdapter bluetoothAdapter;

    private DiscoverBLE() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void startScan() {
        String serviceUUID = BluetoothUtils.SERVICE_UUID;
        checkAdvertiseBLE();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);

                    isStart = true;
                    if (result != null) {
                        BluetoothDevice device = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            device = result.getDevice();
                        }

                        discoverListener.onDiscoverScanResult(device);
                    }
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);

                    isStart = false;
                    discoverListener.onScanFail(errorCode);
                }
            };

            ParcelUuid uuid = new ParcelUuid(UUID.fromString(serviceUUID));

            List<ScanFilter> filters = new ArrayList<>();
            ScanFilter filter = new ScanFilter.Builder()
                    .setServiceUuid(uuid)
                    .build();
            filters.add(filter);

            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                    .build();

            mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();

            mBluetoothLeScanner.startScan(filters, settings, mCallback);
            if (TIME_OUT != 0) {
                setTimeStopScan();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bluetoothAdapter.startLeScan(leScanCallback);
            }
        }
    }

    BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            discoverListener.onDiscoverScanResult(device);
        }
    };

    public boolean isScanner() {
        return isStart;
    }

    public void stopScan() {
        //stop discover
        //start discover
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.cancelDiscovery();

        isStart = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mBluetoothLeScanner != null)
                mBluetoothLeScanner.stopScan(mCallback);
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bluetoothAdapter.stopLeScan(leScanCallback);
            }
        }
        if (discoverListener != null)
            discoverListener.onDiscoverStopScan();
    }

    private void checkAdvertiseBLE() {
        if (AdvertiseBLE.getInstance().isAdvertising()) {
            AdvertiseBLE.getInstance().stopAdvertise();
        }
    }

    public void setDiscoverListener(DiscoverListener discoverListener) {
        this.discoverListener = discoverListener;
    }

    public void setTimeOut(long timeOut) {
        TIME_OUT = timeOut;
    }

    private void setTimeStopScan() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        }, TIME_OUT);
    }

}
