package com.example.zhuji.testbluetooth.service;

import java.util.Map;

import com.example.zhuji.testbluetooth.activity.MainActivity;
import com.example.zhuji.testbluetooth.callback.BluetoothCallback;

import android.R.bool;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BluetoothScanThread extends Thread {
	
	private Context context;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothCallback bluetoothCallback;
	private Handler handler;
	public  BluetoothScanThread(Context  context,Handler handler) 
	{
		this.context = context;
		this.handler = handler;
		bluetoothCallback = new BluetoothCallback();
		initBluetooth();
	}

	@Override
	public void run() 
	{
		while (bluetoothCallback.getBluetoothDevice().size() < 1) 
		{
			try 
			{
	         	Log.e(" test bluetooth ", "mBluetoothAdapter.startLeScan(callback);");
	            mBluetoothAdapter.startLeScan(bluetoothCallback);
				Thread.sleep(200);
				mBluetoothAdapter.stopLeScan(bluetoothCallback);
	            Log.e(" test bluetooth ", " mBluetoothAdapter.stopLeScan(callback);");
	            
	             Message msg = new Message();
	           	 msg.arg1=2;
	           	 msg.arg2=bluetoothCallback.getBluetoothDevice().size();
	           	 handler.sendMessage(msg);
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
		}
		
		((MainActivity)context).process();
		 
	}
	
	 private void initBluetooth()
    {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null)
        {
            Log.e(" test bluetooth ","mBluetoothAdapter == null");
            return;
        }
    }
	
	 
	 public Map<String, BluetoothDevice> getBluetoothDevice()
	 {
		 return bluetoothCallback.getBluetoothDevice();
	 }

}
