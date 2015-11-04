package com.example.zhuji.testbluetooth.callback;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.zhuji.testbluetooth.util.BluetoothGattObject;
import com.example.zhuji.testbluetooth.util.SampleGattAttributes;
import com.unity3d.player.UnityPlayer;

/**
 * Created by zhuji on 2015/9/14.
 */
public class MyBluetoothGattCallback extends BluetoothGattCallback
{
    public Context service;
    private BluetoothGattObject gatt;
    public String TAG = "MyBluetoothGattCallback";

    public MyBluetoothGattCallback(Context service,BluetoothGattObject obj)
    {
        this.service= service;
        this.gatt = obj;
    }

    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
    {
        if (newState == BluetoothProfile.STATE_CONNECTED){
            Log.e(TAG, "onConnectionStateChange  = STATE_CONNECTED");
            broadcastUpdate(gatt, SampleGattAttributes.ACTION_GATT_CONNECTED);
            gatt.discoverServices();
        }
        else if (newState == BluetoothProfile.STATE_DISCONNECTED){
            Log.e(TAG, "onConnectionStateChange  = STATE_DISCONNECTED" );
            broadcastUpdate(gatt, SampleGattAttributes.ACTION_GATT_DISCONNECTED);
        }

    }
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status)
    {
        Log.e(TAG, "&&&&& onServicesDiscovered");
        if (status == BluetoothGatt.GATT_SUCCESS)
        {

           this.gatt.setBluetoothGattCharacteristics();
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic,int status) {
        Log.e(TAG, "&&&&& onCharacteristicRead");
        if (status == BluetoothGatt.GATT_SUCCESS)
        {
            if (SampleGattAttributes.UUID_PRESSURE_SENSOR_MEASUREMENT.equals(characteristic.getUuid()))
            {
                broadcastUpdate(gatt,SampleGattAttributes.ACTION_GATT_PRESSURE_SENSOR_DATA, characteristic);
            }
            else if (SampleGattAttributes.UUID_POWER_MEASUREMENT.equals(characteristic.getUuid()))
            {
                broadcastUpdate(gatt,SampleGattAttributes.ACTION_POWER_DATA, characteristic);
            }
            else
            {
                broadcastUpdate(gatt,SampleGattAttributes.ACTION_DATA_AVAILABLE, characteristic);
            }
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
    {
        Log.e(TAG, "onCharacteristicChanged");
        if (SampleGattAttributes.UUID_PRESSURE_SENSOR_MEASUREMENT.equals(characteristic.getUuid()))
        {
            broadcastUpdate(gatt, SampleGattAttributes.ACTION_GATT_PRESSURE_SENSOR_DATA, characteristic);
        }
        else if (SampleGattAttributes.UUID_POWER_MEASUREMENT.equals(characteristic.getUuid()))
        {
            broadcastUpdate(gatt,SampleGattAttributes.ACTION_POWER_DATA, characteristic);
        }
        else
        {
            broadcastUpdate(gatt,SampleGattAttributes.ACTION_DATA_AVAILABLE, characteristic);
        }

    }

    private void broadcastUpdate(BluetoothGatt gatt,final String action)
    {
        if (action.equals(SampleGattAttributes.ACTION_GATT_CONNECTED))
        {
            if(gatt.getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                final Intent intent = new Intent(SampleGattAttributes.ACTION_GATT_FIRST_CONNECTED);
                service.sendBroadcast(intent);
            }

        }
        else if (action.equals(SampleGattAttributes.ACTION_GATT_DISCONNECTED))
        {
            if(gatt.getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                final Intent intent = new Intent(SampleGattAttributes.ACTION_GATT_FIRST_DISCONNECTED);
                service.sendBroadcast(intent);
            }

        }
        else if (action.equals(SampleGattAttributes.ACTION_GATT_SERVICES_DISCOVERED))
        {
            if(gatt.getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                final Intent intent = new Intent(SampleGattAttributes.ACTION_GATT_FIRST_SERVICES_DISCOVERED);
                service.sendBroadcast(intent);
            }

        }

    }

    private void broadcastUpdate(BluetoothGatt gatt,final String action, final BluetoothGattCharacteristic characteristic) {
        final byte[] data = characteristic.getValue();
        StringBuilder stringBuilder = new StringBuilder(0);
        if (data != null && data.length > 0)
        {
            stringBuilder = new StringBuilder(data.length);
            for(byte byteChar : data)
            {
                stringBuilder.append(String.format("%02X ", byteChar));
            }
        }

        if (action.equals(SampleGattAttributes.ACTION_GATT_PRESSURE_SENSOR_DATA))
        {
            Log.e("onCharacteristicChanged","UUID_PRESSURE_SENSOR_MEASUREMENT");
            final Intent intent = new Intent(SampleGattAttributes.ACTION_FIRST_PRESSURE_SENSOR_DATA);
            String[] strings= {gatt.getDevice().getAddress(),stringBuilder.toString()};
            intent.putExtra(SampleGattAttributes.EXTRA_DATA, strings);
            service.sendBroadcast(intent);

        }
        else if (action.equals(SampleGattAttributes.ACTION_POWER_DATA))
        {
            final Intent intent = new Intent(SampleGattAttributes.ACTION_FIRST_POWER_DATA);
            intent.putExtra(SampleGattAttributes.EXTRA_DATA, stringBuilder.toString());
            service.sendBroadcast(intent);
        }
        else if (action.equals(SampleGattAttributes.ACTION_DATA_AVAILABLE))
        {
            final Intent intent = new Intent(SampleGattAttributes.ACTION_DATA_AVAILABLE);
            
            intent.putExtra(SampleGattAttributes.EXTRA_DATA,stringBuilder.toString());
            service.sendBroadcast(intent);
        }
    }
    
   



}
