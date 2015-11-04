package com.example.zhuji.testbluetooth.broadcastreceiver;

import android.R.integer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import com.example.zhuji.testbluetooth.util.SampleGattAttributes;
import com.example.zhuji.testbluetooth.util.Util;
import com.unity3d.player.UnityPlayer;

/**
 * Created by zhuji on 2015/9/14.
 */
public class MyBroadcastReceiver extends BroadcastReceiver{
    private boolean mConnected =false;
    private Handler handler ;
    public MyBroadcastReceiver(Handler handler) {
		// TODO Auto-generated constructor stub
    	this.handler = handler;
    
		//initSocket();
		
	}
    
    public Socket socket ;
    PrintWriter os;
    public void initSocket() 
    {
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					socket=new Socket("192.168.0.109",8900);
					os=new PrintWriter(socket.getOutputStream());	
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			   
				
			}
		}).start();
	    
    }

   
    @Override
    public void onReceive(Context context, Intent intent)
    {
        final String action = intent.getAction();
        if (SampleGattAttributes.ACTION_GATT_FIRST_CONNECTED.equals(action))
        {
            mConnected = true;
        }
        else if (SampleGattAttributes.ACTION_GATT_FIRST_DISCONNECTED.equals(action))
        {
            mConnected = false;
        }
        else if (SampleGattAttributes.ACTION_GATT_FIRST_SERVICES_DISCOVERED.equals(action))
        {
           // displayGattServices(mBluetoothLeService.getSupportedGattServices(callback.getBluetoothDevice()));
        }
        else if (SampleGattAttributes.ACTION_FIRST_PRESSURE_SENSOR_DATA.equals(action))
        {
            displayPressureSensorData(intent.getStringArrayExtra(SampleGattAttributes.EXTRA_DATA));
        }
        else if (SampleGattAttributes.ACTION_FIRST_POWER_DATA.equals(action))
        {
            displayPowerData(intent.getStringExtra(SampleGattAttributes.EXTRA_DATA));
        }
        else if (SampleGattAttributes.ACTION_DATA_AVAILABLE.equals(action))
        {
            displayData(intent.getStringExtra(SampleGattAttributes.EXTRA_DATA));
        }
    }

    private void displayData(String data)
    {
        if (data != null)
        {
            Log.e("displayData", data);
        }
    }
    
    
    private Map<String, int[]> sensorDataMap = new HashMap<>();
    private Map<String, status> sensorStatuMap = new HashMap<>();

    private void displayPressureSensorData(String[] data) {
        String subdata;
        int iValue;
        if (data != null)
        {
        	if (!sensorDataMap.containsKey(data[0])) {
        		sensorDataMap.put(data[0], new int[8]);
			}
        	if (!sensorStatuMap.containsKey(data[0])) {
        		sensorStatuMap.put(data[0],  status.STEP_UP);
			}
        	status statu = sensorStatuMap.get(data[0]);
        	final int[] sensorData = sensorDataMap.get(data[0]) ;
        	
            //mSensorUpData.setText(data);
            subdata = data[1].substring(15,17)+data[1].substring(12,14);
            iValue = Integer.parseInt(subdata, 16);
            if (iValue>2048)
            {
                sensorData[0] = 0;
            }
            else
            {
                sensorData[0] = 2048 - iValue;
            }

            subdata = data[1].substring(9,11)+data[1].substring(6,8);
            iValue = Integer.parseInt(subdata, 16);
            if (iValue>2048)
            {
                sensorData[1] = 0;
            }

            else
            {
                sensorData[1] = 2048 - iValue;
            }
            subdata = data[1].substring(21,23)+data[1].substring(18,20);
            iValue = Integer.parseInt(subdata, 16);
            if (iValue>2048)
            {
                sensorData[2] = 0;
            }
            else
            {
                sensorData[2] = 2048 - iValue;
            }
            subdata = data[1].substring(3,5)+data[1].substring(0,2);
            iValue = Integer.parseInt(subdata, 16);
            if (iValue>2048)
            {
                sensorData[3] = 0;
            }
            else
            {
                sensorData[3] = 2048 - iValue;
            }
            
            
           
            /*if (sensorData[0]< threadhold ) {
            	//sensorData[0] = 0;
			}
            else
            {
            	sensorData[0] = 2048;
            }
            
            if (sensorData[1]< threadhold ) {
            	//sensorData[1] = 0;
			}
            else
            {
            	sensorData[1] = 2048;
            }
            if (sensorData[2]< threadhold ) {
            	//sensorData[2] = 0;
			}
            else
            {
            	sensorData[2] = 2048;
            }
            if (sensorData[3]< threadhold ) {
            	//sensorData[3] = 0;
			}else
            {
            	sensorData[3] = 2048;
            }*/
            int threadhold= 1600;
            if (sensorData[0]> threadhold && sensorData[1]> threadhold
            		&&sensorData[2]> threadhold
            		//&&sensorData[3]> threadhold
            		&& statu == status.STEP_UP) {
            	step_count++;
            	statu = status.STEP_DOWN;
            	
            	 Message msg = new Message();
            	 msg.arg1=1;
            	 msg.arg2=step_count;
            	 handler.sendMessage(msg);
            	 Log.e("step_count",step_count+"");
			}
            
            else if (sensorData[0]==0 && sensorData[1]==0
            		&&sensorData[2]==0&&sensorData[3]==0
            		&& statu == status.STEP_DOWN) {

            	statu = status.STEP_UP;
				
			}
            
            sensorStatuMap.put(data[0],  statu);
            
           /* new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					os.write( sensorData[0]+ "  " + sensorData[1]+"  " + sensorData[2]+"   " + sensorData[3]);
		            os.flush();
				}
			}).start();*/
            
            Log.e("step_count", "up = " + sensorData[0]+ "left = " + sensorData[1]+"right = " + sensorData[2]+" back = " + sensorData[3]);
        }
        
    }
    public int step_count =0;
   
    public enum status{STEP_DOWN,STEP_UP};

    private void displayPowerData(String data)
    {
        String subdata;

        int iValue;
        if (data != null)
        {
            subdata = data.substring(3,5)+ data.substring(0,2);

            iValue = Integer.parseInt(subdata, 16);
            Log.e("displayPowerData",String.valueOf((double) iValue / 4095 * 1.25 / 3.9 * 13.9)+" V");
            subdata = data.substring(9,11)+data.substring(6,8);
            iValue = Integer.parseInt(subdata, 16);
            Log.e("displayPowerData", "&&&&& mVoltageData" + iValue);
            if (iValue<1000)
                Log.e("displayPowerData", "0.0000 V");
            else
                Log.e("displayPowerData", String.valueOf((double) iValue / 4095 * 1.25 / 3.0 * 13.0) + " V");
        }
    }

 

}
