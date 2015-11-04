package com.example.zhuji.testbluetooth.util;

import com.unity3d.player.UnityPlayer;

import android.content.IntentFilter;

public class Util {
	public static void reflactCall(String method,String str) {
		 UnityPlayer.UnitySendMessage("Main Camera",method, str);
	}

	public static IntentFilter makeGattUpdateIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SampleGattAttributes.ACTION_GATT_FIRST_CONNECTED);
		intentFilter.addAction(SampleGattAttributes.ACTION_GATT_FIRST_DISCONNECTED);
		intentFilter.addAction(SampleGattAttributes.ACTION_GATT_FIRST_SERVICES_DISCOVERED);
		intentFilter.addAction(SampleGattAttributes.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(SampleGattAttributes.ACTION_FIRST_PRESSURE_SENSOR_DATA);
		intentFilter.addAction(SampleGattAttributes.ACTION_FIRST_POWER_DATA);
		return intentFilter;
	}
	
	public static void BLESHOES_ErrorNotify(String msg){
		//status_msg = "Error Message:"+msg;
		reflactCall("BLESHOES_ErrorNotify",msg);
	}

	public static void BLESHOES_GetShoesStatus(String msg){
		//status_msg = "Status:"+ msg;
		reflactCall("BLESHOES_GetShoesStatus",msg);
	}

	public static void BLESHOES_UpdatePowerData(String msg){
		//power_msg = "power is " + msg;
		reflactCall("BLESHOES_UpdatePowerData",msg);
	}
	

	//private int stepNum=0;
	public static void BLESHOES_UpdateStep(String msg){
	//	stepNum++;
		//step_msg = "step:" + stepNum.ToString ()+" is "+msg+ " shoes";
		reflactCall("BLESHOES_UpdateStep",msg);
	}

	public static void BLESHOES_UpdateClick(String msg){
		//click_msg = msg;
		reflactCall("BLESHOES_UpdateClick",msg);
	}

}
