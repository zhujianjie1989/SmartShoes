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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingDeque;

import com.example.zhuji.testbluetooth.util.BluetoothGattObject;

public class BluetoothService {

    private BluetoothManager  mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String TAG = "BluetoothService";
    private int shoes_num = 2;
    
    private Map<String, BluetoothGattObject> mDevice ;
    public Context context;
    public BluetoothService(Context context) {
		this.context = context;
		
	}


    public boolean initialize()
    {
        if (mBluetoothManager == null)
        {
            mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
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

    public void connect(Map<String,BluetoothDevice> device)
    {
    
    	/*if (mDevice.containsKey(device.getAddress())) {
			return;
		}*/
    	mDevice = null;
    	mDevice= new HashMap<String, BluetoothGattObject>();
    	Iterator<String> iterator = device.keySet().iterator();
    	while(iterator.hasNext())
    	{
    		String key = iterator.next();
    		BluetoothGattObject gattObject = new BluetoothGattObject(context);
        	gattObject.connect(device.get(key));
            mDevice.put(key, gattObject);
    		
    	}
        
    }

    public void close()
    {
    	Iterator<String> iterator = mDevice.keySet().iterator();
    	while(iterator.hasNext())
    	{
    		String key = iterator.next();
    		BluetoothGattObject gatt = mDevice.get(key);
    		gatt.close();
    		stopAutoHeartBeat();
    	}
       
    }

    
    
    public void disconnect()
    {
    	Iterator<String> iterator = mDevice.keySet().iterator();
    	while(iterator.hasNext())
    	{
    		String key = iterator.next();
    		BluetoothGattObject gatt = mDevice.get(key);
    		gatt.disconnect();
    	}
    }

    public void startGatt(){
    	Iterator<String> iterator = mDevice.keySet().iterator();
    	while(iterator.hasNext())
    	{
    		String key = iterator.next();
    		BluetoothGattObject gatt = mDevice.get(key);
    		gatt.startGatt();
    		setupAutoHeartBeat();
    	}
    	
    	
    }
    private Timer mTimer ;
    private void setupAutoHeartBeat()
    {
    	if (mTimer!=null) {
			return ;
		}
    	
        mTimer = new Timer();
        mTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                Log.e(TAG, "autoSendHeart");
            	autoSendHeart();
            }
        }, 1000, 1000);
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
    
    public void stopAutoHeartBeat()
    {

    	if (mTimer!=null) {
			mTimer.cancel();
			mTimer = null;
		}
    }
    
    public boolean isReady()
    {
    	Iterator<String> iterator = mDevice.keySet().iterator();
    	while(iterator.hasNext())
    	{
    		String key = iterator.next();
    		BluetoothGattObject gatt = mDevice.get(key);
    		if (!gatt.isCharacteristicReady()) {
				return false;
			}
    	}
    	
    	return true;
    }
    
    public void stopGatt()
    {
    	Iterator<String> iterator = mDevice.keySet().iterator();
    	while(iterator.hasNext())
    	{
    		String key = iterator.next();
    		BluetoothGattObject gatt = mDevice.get(key);
    		gatt.stopGatt();
    	}	
    }

}
