package com.example.zhuji.testbluetooth.activity;

import android.R.bool;
import android.R.string;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zhuji.testbluetooth.callback.BluetoothCallback;
import com.example.zhuji.testbluetooth.service.BluetoothScanThread;
import com.example.zhuji.testbluetooth.service.BluetoothService;
import com.example.zhuji.testbluetooth.R;
import com.example.zhuji.testbluetooth.broadcastreceiver.MyBroadcastReceiver;
import com.example.zhuji.testbluetooth.util.SampleGattAttributes;
import com.example.zhuji.testbluetooth.util.Util;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity  extends UnityPlayerActivity //Activity
{

   
    private String  TAG = "MainActivity";
    private BluetoothService mBluetoothLeService;
    private BluetoothAdapter mBluetoothAdapter= null;
    private BluetoothScanThread bluetoothScanThread;
    //private BluetoothCallback bluetoothCallback = new BluetoothCallback();
    private  MyBroadcastReceiver mGattUpdateReceiver ;
    /*TextView shoes ;
	TextView step ;*/
    private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.arg1 == 1) 
			{
				String string = (String)msg.obj;
				 Util.BLESHOES_UpdateStep( string);
			}
			else if (msg.arg1 == 2)
			{
           	
			}
			else if (msg.arg1 == 3) 
			{
	           	 Util.BLESHOES_UpdatePowerData( msg.obj.toString()+"");
			}
			else if (msg.arg1 == 4) 
			{
	           	 Util.BLESHOES_GetShoesStatus( msg.obj.toString()+"");
			}
			else if (msg.arg1 == 5) 
			{
	           	 Util.BLESHOES_UpdateClick( msg.obj.toString()+"");
			}
		}
    	
    };
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏 
        mGattUpdateReceiver = new MyBroadcastReceiver(handler);
        initBluetooth();
      //  initUI();
       /* step = (TextView)findViewById(R.id.TV_step); 
        shoes = (TextView)findViewById(R.id.TV_shoes);*/
        bluetoothScanThread = new BluetoothScanThread(this,handler);
        //bluetoothScanThread.start();
    }

   /* private void initUI()
    {
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	if (bluetoothScanThread.getBluetoothDevice()==null) {
					return;
				}

                if (mBluetoothLeService != null)
                {
                    mBluetoothLeService.startGatt();
                    setupAutoHeartBeat();
                }
            }

        });
    }*/


    private void initBluetooth()
    {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null)
        {
            Log.e(" test bluetooth ","mBluetoothAdapter == null");
            Util.BLESHOES_ErrorNotify("please open bluetooth and then reinitial");
            return;
        }
    }

    
    
    @Override
    protected void onStop() {
    	super.onStop();
	    unregisterReceiver(mGattUpdateReceiver);
	    mBluetoothLeService = null;
	    Log.e(TAG, "onStop");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        
        registerReceiver(mGattUpdateReceiver, Util.makeGattUpdateIntentFilter());
    }
    
    public boolean callfunction()
    {
    	
    	if (bluetoothScanThread.getBluetoothDevice()==null) {
			return false;
		}

        if (mBluetoothLeService != null)
        {
            mBluetoothLeService.startGatt();
        }
        
    	
        Log.e(" callfunction ", "callfunction");
        return true;

    }
    
    private  void BLESHOES_InitShoes(String leftValue,String rightValue)
    {
    	Util.BLESHOES_UpdateClick("BLESHOES_InitShoes");
    	bluetoothScanThread = new BluetoothScanThread(this,handler);
    	bluetoothScanThread.setScanList(leftValue, rightValue);
    	Util.setScanlist(leftValue, rightValue);
    	
    }


	private  void BLESHOES_ScanShoes()
    {
		Util.BLESHOES_UpdateClick("BLESHOES_ScanShoes");
		bluetoothScanThread.start();
    }


	private  void BLESHOES_StartShoes()
    {
		Util.BLESHOES_UpdateClick("BLESHOES_StartShoes");
		if (bluetoothScanThread.getBluetoothDevice().size()==0) {
			return;
		}

        mBluetoothLeService.startGatt();
           
        
    }


	private  void BLESHOES_StopShoes()
    {
		Util.BLESHOES_UpdateClick("BLESHOES_StopShoes");
		 if (mBluetoothLeService != null&& mBluetoothLeService.isReady())
	     {
	        mBluetoothLeService.stopGatt();
	     }
		
    }


	private  void BLESHOES_CloseShoes()
    {
		Util.BLESHOES_UpdateClick("BLESHOES_CloseShoes");
		 if (mBluetoothLeService != null&& mBluetoothLeService.isReady())
	     {
	        mBluetoothLeService.close();
	        mBluetoothLeService.mDevice.clear();
	     }
    }
	
	private void BLESHOES_DoVibrate(String side)
	{
		Util.BLESHOES_UpdateClick("BLESHOES_DoVibrate");
		Log.e("BLESHOES_DoVibrate", side);
		Log.e("BLESHOES_DoVibrate", Util.getSideByMac(side));
		if (Util.getSideByMac(side)!=null) {
			mBluetoothLeService.DoVibrate(Util.getSideByMac(side));
		}
		
	}

	public void process() {
		if (mBluetoothLeService == null) 
    	{
    		mBluetoothLeService = new BluetoothService(this);
		}
       

        if (!mBluetoothLeService.initialize())
        {
            Log.e(TAG, "Unable to initialize Bluetooth");
            finish();
        }
        Log.e(TAG, "success to initialize Bluetooth");
        mBluetoothLeService.connect(bluetoothScanThread.getBluetoothDevice());  
		
	}
	
	public void process(BluetoothDevice device) {
		if (mBluetoothLeService == null) 
    	{
    		mBluetoothLeService = new BluetoothService(this);
		}
       
        Log.e(TAG, "success to initialize Bluetooth");
        mBluetoothLeService.connect(device);  
		
	}
   
}
