package com.bluetooth.ble.advertise;

public interface AdvertiseListener {
    void onAdvertiseSuccess();

    void onAdvertiseFail(int errorCode);
}
