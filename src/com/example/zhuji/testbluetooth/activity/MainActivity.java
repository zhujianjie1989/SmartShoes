package com.example.zhuji.testbluetooth.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.zhuji.testbluetooth.callback.BluetoothCallback;
import com.example.zhuji.testbluetooth.service.BluetoothService;
import com.example.zhuji.testbluetooth.R;
import com.example.zhuji.testbluetooth.broadcastreceiver.MyBroadcastReceiver;
import com.example.zhuji.testbluetooth.util.SampleGattAttributes;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity  extends UnityPlayerActivity
{

    private Timer mTimer ;
    private boolean flag = true;
    private String  TAG = "MainActivity";
    private BluetoothService mBluetoothLeService;
    private BluetoothAdapter mBluetoothAdapter= null;
    private BluetoothCallback bluetoothCallback = new BluetoothCallback();
    private final MyBroadcastReceiver mGattUpdateReceiver = new MyBroadcastReceiver();
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        initBluetooth();
        //initUI();
    }

    private void initUI()
    {
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (mBluetoothLeService != null)
                {
                    mBluetoothLeService.startGatt();
                    setupAutoHeartBeat();
                }
                else
                {
                    handler.sendEmptyMessage(1);
                }

            }

        });
    }


    private void initBluetooth()
    {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null)
        {
            Log.e(" test bluetooth ","mBluetoothAdapter == null");
            finish();
            return;
        }

        handler.sendEmptyMessage(1);
    }


    private void setupAutoHeartBeat()
    {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                Log.e(TAG, "autoSendHeart");
                mBluetoothLeService.autoSendHeart();
            }
        }, 1000, 1000);
    }

    private  IntentFilter makeGattUpdateIntentFilter()
    {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SampleGattAttributes.ACTION_GATT_FIRST_CONNECTED);
        intentFilter.addAction(SampleGattAttributes.ACTION_GATT_FIRST_DISCONNECTED);
        intentFilter.addAction(SampleGattAttributes.ACTION_GATT_FIRST_SERVICES_DISCOVERED);
        intentFilter.addAction(SampleGattAttributes.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(SampleGattAttributes.ACTION_FIRST_PRESSURE_SENSOR_DATA);
        intentFilter.addAction(SampleGattAttributes.ACTION_FIRST_POWER_DATA);
        return intentFilter;
    }


    public void process()
    {
        Intent intent = new Intent(this,BluetoothService.class);
        bindService(intent,mServiceConnection,BIND_AUTO_CREATE);
    }


    private final ServiceConnection mServiceConnection = new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service)
        {
            Log.e(TAG, "onServiceConnected");
            mBluetoothLeService = ((BluetoothService.LocalBinder) service).getService();

            if (!mBluetoothLeService.initialize())
            {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }

            mBluetoothLeService.connect(bluetoothCallback.getBluetoothDevice());
            Log.e(TAG, "success to initialize Bluetooth");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            mBluetoothLeService = null;
            Log.e(TAG, "onServiceDisconnected");
        }
    };

    @Override
    protected void onPause()
    {
        super.onPause();
       
    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	 unbindService(mServiceConnection);
         unregisterReceiver(mGattUpdateReceiver);
         mBluetoothLeService = null;
         Log.e(TAG, "onStop");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }


    private Handler handler  = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (bluetoothCallback.getBluetoothDevice() == null)
            {
                if (flag)
                {
                    Log.e(" test bluetooth ", "mBluetoothAdapter.startLeScan(callback);");
                    mBluetoothAdapter.startLeScan(bluetoothCallback);
                }
                else
                {
                    mBluetoothAdapter.stopLeScan(bluetoothCallback);
                    Log.e(" test bluetooth ", " mBluetoothAdapter.stopLeScan(callback);");
                }
                flag = !flag;
                this.sendEmptyMessageDelayed(1, 1000);

            }
            else
            {
                if (!flag)
                {
                    Log.e(" test bluetooth ", "mBluetoothAdapter.stopLeScan(callback);");
                    mBluetoothAdapter.stopLeScan(bluetoothCallback);
                    flag = !flag;
                }

                process();
            }
        }
    };
    
    public void callfunction()
    {
    	if (mBluetoothLeService != null)
        {
            mBluetoothLeService.startGatt();
            setupAutoHeartBeat();
        }
        else
        {
            handler.sendEmptyMessage(1);
        }
        Log.e(" callfunction ", "callfunction");
        reflactCall("hkjfdshgksdhfghsdk");

    }
    
    public void reflactCall(String str)
    {
    	UnityPlayer.UnitySendMessage("Main Camera","AndroidReceive", str);
    }
    
    

}
