package com.example.zhuji.testbluetooth.callback;

import java.net.ContentHandler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.zhuji.testbluetooth.activity.MainActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

/**
 * Created by zhuji on 2015/9/11.
 */
public class BluetoothCallback implements BluetoothAdapter.LeScanCallback {
    private final static String TAG = BluetoothCallback.class.getSimpleName();
    private Context context ;
    public BluetoothCallback(Context context) {
		this.context = context;
	}
    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.e(TAG, "name = " + device.getName() + " address =  " + device.getAddress());
        //78:C5:E5:6E:D1:0C
        if (scanlist.containsKey(device.getAddress())) 
        {
        	//Log.e(TAG, "name = " + device.getName() + " address =  " + device.getAddress());
            this.devices.put(device.getAddress(), device);
            ((MainActivity)context).process(device);
        }
    }
    //BluetoothDevice 
    public Map<String,BluetoothDevice>devices = new HashMap<String, BluetoothDevice>();
    public Map<String,String> scanlist = new HashMap<String,String>();

    public Map<String,BluetoothDevice> getBluetoothDevice(){
        return  devices;
    }
    
    public void setScanList(String side,String addr)
    {
    	scanlist.put(addr, side);
    	Log.e(addr, side);
    }

	public boolean isStop() {
		if (scanlist.size() == devices.size()) {
			return false;
		}
		
		return true;
	}
	
	public String getSideMac(String side)
	{
		return scanlist.get(side);
	}
    
    
}
