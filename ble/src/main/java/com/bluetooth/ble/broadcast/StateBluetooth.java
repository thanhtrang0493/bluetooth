package com.bluetooth.ble.broadcast;

public enum StateBluetooth {
    DEVICE_CONNECTED("Device Connected"),
    DEVICE_DISCONNECTED("Device Disconnected"),
    SERVICE_DISCOVER_CONNECTED("Service Discover Connected"),
    SERVICE_DISCOVER_DISCONNECTED("Service Discover Disconnected"),
    WRITE_CHARACTERISTIC_SUCCESS("Write Characteristic Success"),
    WRITE_CHARACTERISTIC_FAIL("Write Characteristic Fail");


    private String intValue;

    StateBluetooth(String value) {
        intValue = value;
    }

    public String getValue() {
        return intValue;
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }
}
