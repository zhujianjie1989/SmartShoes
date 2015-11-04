package com.example.zhuji.testbluetooth.callback;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

/**
 * Created by zhuji on 2015/9/11.
 */
public class BluetoothCallback implements BluetoothAdapter.LeScanCallback {
    private final static String TAG = BluetoothCallback.class.getSimpleName();
    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.e(TAG, "name = " + device.getName() + " address =  " + device.getAddress());
        //78:C5:E5:6E:D1:0C
        if (device.getName().equals("BLE SHOES")) {
            this.device = device;
        }
    }
    BluetoothDevice device = null;

    public BluetoothDevice getBluetoothDevice(){
        return  device;
    }
}
