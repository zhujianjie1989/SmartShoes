package com.example.zhuji.testbluetooth.util;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.example.zhuji.testbluetooth.callback.MyBluetoothGattCallback;
import com.unity3d.player.UnityPlayer;

import java.util.List;
import java.util.UUID;

/**
 * Created by zhuji on 2015/9/14.
 */
public class BluetoothGattObject {
    public BluetoothGatt gatt ;
    public Service service ;
    public String TAG = "BluetoothGattObject";
    private MyBluetoothGattCallback myBluetoothGattCallback ;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mCharacteristicSensor;
    private BluetoothGattCharacteristic mCharacteristicPower;
    private BluetoothGattCharacteristic mCharacteristicControl;
    private BluetoothGattCharacteristic mCharacteristicHeart;
    private BluetoothGattCharacteristic mCharacteristicSampFreq;
    public final static UUID UUID_PRESSURE_SENSOR_MEASUREMENT = UUID.fromString(SampleGattAttributes.PRESSURE_SENSOR_MEASUREMENT);
    public final static UUID UUID_POWER_MEASUREMENT =  UUID.fromString(SampleGattAttributes.POWER_MEASUREMENT);
    public final static UUID UUID_CONTROL_SWITCH =  UUID.fromString(SampleGattAttributes.CONTROL_SWITCH);
    public final static UUID UUID_CONTROL_SAMP_FREQ = UUID.fromString(SampleGattAttributes.CONTROL_SAMP_FREQ);
    public final static UUID UUID_HEART_BEAT =  UUID.fromString(SampleGattAttributes.HEART_BEAT);

    private boolean isConnect =false;

    public BluetoothGattObject(Service service){
        this.service= service;
        myBluetoothGattCallback= new MyBluetoothGattCallback(service,this);
    }

    public List<BluetoothGattService> getSupportedGattServices()
    {
        Log.e(TAG, "mBluetoothFirstGatt size = " + gatt.getServices().size());
        return gatt.getServices();
    }

    public void readCharacteristic( BluetoothGattCharacteristic characteristic)
    {
        Log.e(TAG, "readCharacteristic");
        if(gatt == null)
            return;
        gatt.readCharacteristic(characteristic);
    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic)
    {
        Log.e(TAG, "writeCharacteristic  uuid = " + characteristic.getUuid() + " address = " + gatt.getDevice().getAddress());
        if(gatt == null)
            return false;
        Log.e(TAG, "writeCharacteristic   1");
        return gatt.writeCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification( BluetoothGattCharacteristic characteristic, boolean enabled)
    {
        if(gatt == null)
            return;
        Log.w(TAG, "setCharacteristicNotification " + characteristic.getUuid() + " " + enabled);
        Log.e("setCharacteristi","enabled ="+enabled +" statues = " +gatt.setCharacteristicNotification(characteristic, enabled));
        if (UUID_PRESSURE_SENSOR_MEASUREMENT.equals(characteristic.getUuid()) || UUID_POWER_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }
    }

    public void connect(BluetoothDevice device)
    {
         gatt =  device.connectGatt(service,false,myBluetoothGattCallback);
         isConnect = true;
    }

    public boolean isConect()
    {
        return  isConnect;
    }

    public void startGatt()
    {
        if (this.isConect()) {

            setCharacteristicNotification(mCharacteristicSensor, true);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setCharacteristicNotification(mCharacteristicPower, true);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mCharacteristicControl.setValue(5, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            mCharacteristicControl.setValue(1, BluetoothGattCharacteristic.FORMAT_UINT8, 1);
            writeCharacteristic(mCharacteristicControl);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mCharacteristicSampFreq.setValue(1, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            writeCharacteristic(mCharacteristicSampFreq);
            reflactCall("okokokokok");

        }
    }
    
    public void reflactCall(String str)
    {
    	UnityPlayer.UnitySendMessage("Main Camera","AndroidReceive", str);
    }

    public void close()
    {
        if(gatt != null)
        {
            gatt.close();
            gatt = null;
        }
    }

    public void disconnect()
    {
        if(gatt == null)
            return;
        gatt.disconnect();
    }


    public void setBluetoothGattCharacteristics() {

        List<BluetoothGattService> gattServices = gatt.getServices();
        if (gattServices == null)
            return;
        for (BluetoothGattService gattService : gattServices) {
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {

                if (UUID_PRESSURE_SENSOR_MEASUREMENT.equals(gattCharacteristic.getUuid()))
                {
                    Log.e(TAG,"Characteristics uuid = " +gattCharacteristic.getUuid());
                    mCharacteristicSensor = gattCharacteristic;
                    //this.setCharacteristicNotification(mCharacteristicSensor, false);
                    this.readCharacteristic(mCharacteristicSensor);
                }
                else if(UUID_POWER_MEASUREMENT.equals(gattCharacteristic.getUuid()))
                {
                    Log.e(TAG,"Characteristics uuid = " +gattCharacteristic.getUuid());
                    mCharacteristicPower = gattCharacteristic;
                    //this.setCharacteristicNotification( mCharacteristicPower, false);
                    this.readCharacteristic( mCharacteristicPower);
                }
                else if(UUID_CONTROL_SWITCH.equals(gattCharacteristic.getUuid()))
                {
                    Log.e(TAG, "Characteristics uuid = " + gattCharacteristic.getUuid());
                    mCharacteristicControl = gattCharacteristic;
                    //this.setCharacteristicNotification( mCharacteristicControl, false);
                    this.readCharacteristic( mCharacteristicControl);
                    //Log.e(TAG, "&&&&& gattCharacteristic UUID_CONTROL_SWITCH");
                }
                else if(UUID_CONTROL_SAMP_FREQ.equals(gattCharacteristic.getUuid()))
                {
                    Log.e(TAG,"Characteristics uuid = " +gattCharacteristic.getUuid());
                    mCharacteristicSampFreq = gattCharacteristic;
                    //this.setCharacteristicNotification( mCharacteristicSampFreq, false);
                    this.readCharacteristic( mCharacteristicSampFreq);
                    mCharacteristicSampFreq.setValue(4, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    this.writeCharacteristic( mCharacteristicSampFreq);

                }
                else if(UUID_HEART_BEAT.equals(gattCharacteristic.getUuid()))
                {
                    Log.e(TAG,"Characteristics uuid = " +gattCharacteristic.getUuid());
                    mCharacteristicHeart = gattCharacteristic;
                   // this.setCharacteristicNotification(mCharacteristicHeart, false);
                }
            }
        }
    }

    public void autoSendHeart()
    {

            final int charaProp = mCharacteristicHeart.getProperties();
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {

                mCharacteristicHeart.setValue(60, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                this.writeCharacteristic(mCharacteristicHeart);
            }
    }


}
