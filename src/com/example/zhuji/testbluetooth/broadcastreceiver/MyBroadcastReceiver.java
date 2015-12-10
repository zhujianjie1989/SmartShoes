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
import java.util.Iterator;
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
    
   /* public Socket socket ;
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
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			   
				
			}
		}).start();
	    
    }
*/
   
    @Override
    public void onReceive(Context context, Intent intent)
    {
        final String action = intent.getAction();
        if (SampleGattAttributes.ACTION_GATT_CONNECTED.equals(action))
        {
        	displayConnectStatus(intent.getStringArrayExtra(SampleGattAttributes.EXTRA_DATA));
            mConnected = true;
        }
        else if (SampleGattAttributes.ACTION_GATT_DISCONNECTED.equals(action))
        {
        	displayConnectStatus(intent.getStringArrayExtra(SampleGattAttributes.EXTRA_DATA));
            mConnected = false;
        }
        else if (SampleGattAttributes.ACTION_GATT_SERVICES_DISCOVERED.equals(action))
        {
        	displayConnectStatus(intent.getStringArrayExtra(SampleGattAttributes.EXTRA_DATA));
        }
        else if (SampleGattAttributes.ACTION_GATT_PRESSURE_SENSOR_DATA.equals(action))
        {
            displayPressureSensorData(intent.getStringArrayExtra(SampleGattAttributes.EXTRA_DATA));
        }
        else if (SampleGattAttributes.ACTION_POWER_DATA.equals(action))
        {
            displayPowerData(intent.getStringArrayExtra(SampleGattAttributes.EXTRA_DATA));
        }
        else if (SampleGattAttributes.ACTION_DATA_AVAILABLE.equals(action))
        {
            displayData(intent.getStringExtra(SampleGattAttributes.EXTRA_DATA));
        }
    }
    
    private Map<String,String> senserStatusMap = new HashMap<String, String>();
    public void displayConnectStatus(String [] data)
    {
    	if(data == null )
    		return;
    	
    	senserStatusMap.put(data[0], data[1]);
    	 Iterator<String> keys = senserStatusMap.keySet().iterator();
         String arg = "";
         while(keys.hasNext())
         {
         	String key = keys.next();
         	String status = senserStatusMap.get(key);
         	arg += Util.getSideByMac(key) +":"+status+"  ";
         }
         
         Message msg = new Message();
       	 msg.arg1=4;
         msg.obj=arg;
     	 handler.sendMessage(msg);
    }

    private void displayData(String data)
    {
        if (data != null)
        {
            Log.e("displayData", data);
        }
    }
    
    
    private Map<String, int[]> sensorDataMap = new HashMap<String, int[]>();
    private Map<String, status> sensorStatuMap = new HashMap<String, status>();
    public int step_count =0;
    public enum status{STEP_DOWN,STEP_UP,STEP_JUMP_UP,STEP_JUMP_DOWN};
    
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
            
            
            int maxthreadhold= 1500;
            int minthreadhold= 500;
            if (sensorData[0]> maxthreadhold && sensorData[1]> maxthreadhold
            		&&sensorData[2]> maxthreadhold
            		//&&sensorData[3]> threadhold
            		&& statu == status.STEP_UP) {
            	step_count++;
            	statu = status.STEP_DOWN;
            	
            	 Message msg = new Message();
            	 msg.arg1=1;
            	 msg.arg2=step_count;
            	 msg.obj=Util.getSideByMac(data[0]);
            	 handler.sendMessage(msg);
            	 Log.e("step_count",step_count+"");
			}
            
            else if (sensorData[0]<minthreadhold && sensorData[1]<minthreadhold
            		&&sensorData[2]<minthreadhold&&sensorData[3]<minthreadhold
            		&& statu == status.STEP_DOWN) {

            	statu = status.STEP_UP;
				
			}
            
            sensorStatuMap.put(data[0],  statu);
            
            Iterator<String> keys = sensorDataMap.keySet().iterator();
            String arg = "";
            while(keys.hasNext())
            {
            	String key = keys.next();
            	
            	int [] sdata = sensorDataMap.get(key);
            	arg += Util.getSideByMac(key) +":"+"up = " + sdata[0]+ " left = " + sdata[1]+" right = " + sdata[2]+" back = " + sdata[3] +"\n";
            }
            Message msg = new Message();
	       	msg.arg1=5;
	        msg.obj=arg;
	       	handler.sendMessage(msg);
            
            /*Iterator<String> status_keys = sensorStatuMap.keySet().iterator();
            while(status_keys.hasNext())
            {
            	String key = status_keys.next();
            	
            	status st = sensorStatuMap.get(key);
            	if (st != status.STEP_UP) 
            	{
					return;
				}
            	
            } 
            status_keys = sensorStatuMap.keySet().iterator();
            while(status_keys.hasNext())
            {
            	String key = status_keys.next();
            	status st = sensorStatuMap.get(key);
            	st = status.STEP_JUMP_UP;
            } 
            msg = new Message();
	       	msg.arg1=6;
	        msg.obj=arg;
	       //	handler.sendMessage(msg);
*/           
           
        }
        
    }
 

    private Map<String,Integer> senserPowerDataMap = new HashMap<String, Integer>();
    private void displayPowerData(String[] data)
    {
        String subdata;

        int iValue;
        if (data != null)
        {
            subdata = data[1].substring(3,5)+ data[1].substring(0,2);

            iValue = Integer.parseInt(subdata, 16);
            Log.e("displayPowerData",String.valueOf((double) iValue / 4095 * 1.25 / 3.9 * 13.9)+" V");
            subdata = data[1].substring(9,11)+data[1].substring(6,8);
            iValue = Integer.parseInt(subdata, 16);
            senserPowerDataMap.put(data[0], iValue);
            Log.e("displayPowerData", "&&&&& mVoltageData" + iValue);
            
            
            Iterator<String> keys = senserPowerDataMap.keySet().iterator();
            String arg = "";
            while(keys.hasNext())
            {
            	String key = keys.next();
            	double powerData = 0;
            	if (iValue>900)
            		powerData= (double)senserPowerDataMap.get(key)  / 4095 * 1.25 / 3.0 * 13.0 ;
            	
            	
            	arg += Util.getSideByMac(key) +":"+String.format("%.4f", powerData)+"  V";
            }
            
            Message msg = new Message();
	       	msg.arg1=3;
	        msg.obj=arg;
	     	handler.sendMessage(msg);
	     	
	     	
            if (iValue<1000)
                Log.e("displayPowerData", "0.0000 V");
            else
                Log.e("displayPowerData", String.valueOf((double) iValue / 4095 * 1.25 / 3.0 * 13.0) + " V");
        }
    }

 

}
