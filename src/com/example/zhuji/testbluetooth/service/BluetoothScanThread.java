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
	private boolean stop_flag = false;
	public  BluetoothScanThread(Context  context,Handler handler) 
	{
		this.context = context;
		this.handler = handler;
		bluetoothCallback = new BluetoothCallback(context);
		initBluetooth();
	}

	@Override
	public void run() 
	{
		while (bluetoothCallback.isStop()&& !stop_flag) 
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
		
		//((MainActivity)context).process();
		 
	}
	
	public void setScanList(String leftMac,String rightMac)
	{
		if (leftMac!=null) {
			bluetoothCallback.setScanList("LEFT", leftMac);
		}
		if (rightMac!=null) {
			bluetoothCallback.setScanList("RIGHT", rightMac);
		}
		
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
	 
	 public String getSideMac(String side)
	 {
		 return bluetoothCallback.getSideMac(side);
	 }
	 
	 public boolean isStarted()
	 {
		 return bluetoothCallback.devices.size()!=0||this.isAlive()? true:false;
	 }
	 
	 public void stopThread()
	 {
		 stop_flag = true;
	 } 

}
