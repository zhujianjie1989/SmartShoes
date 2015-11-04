package com.example.zhuji.testbluetooth.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.example.zhuji.testbluetooth.util.BluetoothGattObject;

public class BluetoothService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private BluetoothManager  mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String TAG = "BluetoothService";
    
    private Map<String, BluetoothGattObject> mDevice= new HashMap<String, BluetoothGattObject>();

    public class LocalBinder extends Binder
    {
        public BluetoothService getService()
        {
            return BluetoothService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        close();
        return super.onUnbind(intent);
    }

    public boolean initialize()
    {
        if (mBluetoothManager == null)
        {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null)
            {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null)
        {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        
         

        return true;
    }

    public void connect(BluetoothDevice device)
    {
    	if (mDevice.size() >=2) {
			return;
		}
    	BluetoothGattObject gattObject = new BluetoothGattObject(this);
    	gattObject.connect(device);
        mDevice.put(device.getAddress(), gattObject);
        
    }

    public void close()
    {
    	Iterator<String> iterator = mDevice.keySet().iterator();
    	while(iterator.hasNext())
    	{
    		String key = iterator.next();
    		BluetoothGattObject gatt = mDevice.get(key);
    		gatt.close();
    	}
       
    }

    
    
    public void disconnect(BluetoothGatt gatt)
    {
        if (mBluetoothAdapter == null )
        {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        gatt.disconnect();
    }

    public void startGatt(){
    	Iterator<String> iterator = mDevice.keySet().iterator();
    	while(iterator.hasNext())
    	{
    		String key = iterator.next();
    		BluetoothGattObject gatt = mDevice.get(key);
    		gatt.startGatt();;
    	}
    }

    public void autoSendHeart() {
    	Iterator<String> iterator = mDevice.keySet().iterator();
    	while(iterator.hasNext())
    	{
    		String key = iterator.next();
    		BluetoothGattObject gatt = mDevice.get(key);
    		gatt.autoSendHeart();;
    	}
    }

}
