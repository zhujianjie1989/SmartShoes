package com.example.zhuji.testbluetooth.util;

import android.R.bool;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import com.example.zhuji.testbluetooth.callback.MyBluetoothGattCallback;
import com.unity3d.player.UnityPlayer;

import java.util.List;
import java.util.UUID;

/**
 * Created by zhuji on 2015/9/14.
 */
public class BluetoothGattObject {
	public BluetoothGatt gatt;
	public Context service;
	private boolean isStartGatt = false;
	public String TAG = "BluetoothGattObject";
	private MyBluetoothGattCallback myBluetoothGattCallback;
	private BluetoothGattCharacteristic mNotifyCharacteristic;
	private BluetoothGattCharacteristic mCharacteristicSensor;
	private BluetoothGattCharacteristic mCharacteristicPower;
	private BluetoothGattCharacteristic mCharacteristicControl;
	private BluetoothGattCharacteristic mCharacteristicHeart;
	private BluetoothGattCharacteristic mCharacteristicSampFreq;
	public final static UUID UUID_PRESSURE_SENSOR_MEASUREMENT = UUID
			.fromString(SampleGattAttributes.PRESSURE_SENSOR_MEASUREMENT);
	public final static UUID UUID_POWER_MEASUREMENT = UUID.fromString(SampleGattAttributes.POWER_MEASUREMENT);
	public final static UUID UUID_CONTROL_SWITCH = UUID.fromString(SampleGattAttributes.CONTROL_SWITCH);
	public final static UUID UUID_CONTROL_SAMP_FREQ = UUID.fromString(SampleGattAttributes.CONTROL_SAMP_FREQ);
	public final static UUID UUID_HEART_BEAT = UUID.fromString(SampleGattAttributes.HEART_BEAT);

	private boolean isConnect = false;

	public BluetoothGattObject(Context service)
	{
		this.service = service;
		myBluetoothGattCallback = new MyBluetoothGattCallback(service, this);
	}

	public List<BluetoothGattService> getSupportedGattServices() 
	{
		Log.e(TAG, "mBluetoothFirstGatt size = " + gatt.getServices().size());
		return gatt.getServices();
	}

	public void readCharacteristic(BluetoothGattCharacteristic characteristic)
	{
		Log.e(TAG, "readCharacteristic");
		if (gatt == null)
			return;
		gatt.readCharacteristic(characteristic);
	}

	public boolean isCharacteristicReady() 
	{
		if (mCharacteristicSensor != null 
				&& mCharacteristicPower != null && mCharacteristicControl != null
				&& mCharacteristicHeart != null && mCharacteristicSampFreq != null) 
		{
			return true;
		}
		
		return false;

	}
	
	public void resetCharacteristic()
	{
		mCharacteristicSensor = null ;
		mCharacteristicPower = null ;
		mCharacteristicControl = null;
		mCharacteristicHeart = null ;
		mCharacteristicSampFreq = null;
	}

	public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) 
	{
		if (!isCharacteristicReady()) 
		{
			return false;
		}
		Log.e(TAG, "writeCharacteristic  uuid = " + characteristic.getUuid() + " address = "
				+ gatt.getDevice().getAddress());
		if (gatt == null)
			return false;
		Log.e(TAG, "writeCharacteristic   1");
		return gatt.writeCharacteristic(characteristic);
	}

	
	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) 
	{
		if (gatt == null)
			return;
		if (!isCharacteristicReady()) 
		{
			return ;
		}
		
		Log.w(TAG, "setCharacteristicNotification " + characteristic.getUuid() + " " + enabled);
		Log.e("setCharacteristi",
				"enabled =" + enabled + " statues = " + gatt.setCharacteristicNotification(characteristic, enabled));
		if (UUID_PRESSURE_SENSOR_MEASUREMENT.equals(characteristic.getUuid())
				|| UUID_POWER_MEASUREMENT.equals(characteristic.getUuid())) 
		{
			BluetoothGattDescriptor descriptor = characteristic
					.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			gatt.writeDescriptor(descriptor);
		}
	}

	public void connect(BluetoothDevice device)
	{
		gatt = device.connectGatt(service, false, myBluetoothGattCallback);
		isConnect = true;
	}

	public boolean isConect() 
	{
		return isConnect;
	}

	public void startGatt() {
		if (!isCharacteristicReady()) 
		{
			isStartGatt = true;
			return ;
		}
		
		if (this.isConect()) {

			setCharacteristicNotification(mCharacteristicSensor, true);
			try {
				Thread.sleep(100);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			setCharacteristicNotification(mCharacteristicPower, true);
			try {
				Thread.sleep(100);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			mCharacteristicControl.setValue(5, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
			mCharacteristicControl.setValue(1, BluetoothGattCharacteristic.FORMAT_UINT8, 1);
			writeCharacteristic(mCharacteristicControl);
			try {
				Thread.sleep(100);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			mCharacteristicSampFreq.setValue(1, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
			writeCharacteristic(mCharacteristicSampFreq);

		}
	}
	
	public void stopGatt() 
	{
		if (isCharacteristicReady()&&this.isConect()) 
		{
			setCharacteristicNotification(mCharacteristicSensor, false);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			mCharacteristicControl.setValue(5, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
			mCharacteristicControl.setValue(1, BluetoothGattCharacteristic.FORMAT_UINT8, 1);
			writeCharacteristic(mCharacteristicControl);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void disconnect() 
	{
		if (gatt != null) 
		{
			gatt.disconnect();
			gatt = null;
		}
	}

	public void close() 
	{
		if (gatt == null)
			return;
		
		if (isCharacteristicReady()&&this.isConect()) 
		{
			setCharacteristicNotification(mCharacteristicSensor, false);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			setCharacteristicNotification(mCharacteristicPower, false);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			mCharacteristicControl.setValue(5, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
			mCharacteristicControl.setValue(0, BluetoothGattCharacteristic.FORMAT_UINT8, 1);
			writeCharacteristic(mCharacteristicControl);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			disconnect();
		}
	
	}

	public void setBluetoothGattCharacteristics() 
	{

		List<BluetoothGattService> gattServices = gatt.getServices();
		if (gattServices == null)
			return;
		for (BluetoothGattService gattService : gattServices) {
			List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

			for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {

				if (UUID_PRESSURE_SENSOR_MEASUREMENT.equals(gattCharacteristic.getUuid())) {
					Log.e(TAG, "Characteristics uuid = " + gattCharacteristic.getUuid());
					mCharacteristicSensor = gattCharacteristic;
					
					this.readCharacteristic(mCharacteristicSensor);
				} else if (UUID_POWER_MEASUREMENT.equals(gattCharacteristic.getUuid())) {
					Log.e(TAG, "Characteristics uuid = " + gattCharacteristic.getUuid());
					mCharacteristicPower = gattCharacteristic;
					
					this.readCharacteristic(mCharacteristicPower);
				} else if (UUID_CONTROL_SWITCH.equals(gattCharacteristic.getUuid())) {
					Log.e(TAG, "Characteristics uuid = " + gattCharacteristic.getUuid());
					mCharacteristicControl = gattCharacteristic;
					this.readCharacteristic(mCharacteristicControl);

				} else if (UUID_CONTROL_SAMP_FREQ.equals(gattCharacteristic.getUuid())) {
					Log.e(TAG, "Characteristics uuid = " + gattCharacteristic.getUuid());
					mCharacteristicSampFreq = gattCharacteristic;
					
					this.readCharacteristic(mCharacteristicSampFreq);
					mCharacteristicSampFreq.setValue(4, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
					this.writeCharacteristic(mCharacteristicSampFreq);

				} else if (UUID_HEART_BEAT.equals(gattCharacteristic.getUuid())) {
					Log.e(TAG, "Characteristics uuid = " + gattCharacteristic.getUuid());
					mCharacteristicHeart = gattCharacteristic;
					
				}
			}
			
			if (isCharacteristicReady() && isStartGatt)
			{
				startGatt();
			}
		}
	}
	
	

	public void autoSendHeart() 
	{

		final int charaProp = mCharacteristicHeart.getProperties();
		if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0)
		{

			mCharacteristicHeart.setValue(60, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
			this.writeCharacteristic(mCharacteristicHeart);
		}
	}
	
	

}
