/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.zhuji.testbluetooth.util;

import java.util.HashMap;
import java.util.UUID;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {

    public final static String EXTRA_DATA =  "com.example.bluetooth.le.EXTRA_DATA";

    public final static String ACTION_DATA_AVAILABLE    = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String ACTION_POWER_DATA  = "com.example.bluetooth.le.ACTION_POWER_DATA";
    //public final static String ACTION_FIRST_POWER_DATA  = "com.example.bluetooth.le.ACTION_FIRST_POWER_DATA";
    //public final static String ACTION_SECOND_POWER_DATA = "com.example.bluetooth.le.ACTION_SECOND_POWER_DATA";

    public final static String ACTION_GATT_CONNECTED  = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    //public final static String ACTION_GATT_FIRST_CONNECTED  = "com.example.bluetooth.le.ACTION_GATT_FIRST_CONNECTED";
    //public final static String ACTION_GATT_SECOND_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_SECOND_CONNECTED";

    public final static String ACTION_GATT_DISCONNECTED  = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    //public final static String ACTION_GATT_FIRST_DISCONNECTED   = "com.example.bluetooth.le.ACTION_GATT_FIRST_DISCONNECTED";
    //public final static String ACTION_GATT_SECOND_DISCONNECTED  = "com.example.bluetooth.le.ACTION_GATT_SECOND_DISCONNECTED";

    public final static String ACTION_GATT_PRESSURE_SENSOR_DATA  = "com.example.bluetooth.le.ACTION_GATT_PRESSURE_SENSOR_DATA";
    //public final static String ACTION_FIRST_PRESSURE_SENSOR_DATA  =  "com.example.bluetooth.le.ACTION_FIRST_PRESSURE_SENSOR_DATA";
    //public final static String ACTION_SECOND_PRESSURE_SENSOR_DATA = "com.example.bluetooth.le.ACTION_SECOND_PRESSURE_SENSOR_DATA";

    public final static String ACTION_GATT_SERVICES_DISCOVERED  = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    //public final static String ACTION_GATT_FIRST_SERVICES_DISCOVERED  = "com.example.bluetooth.le.ACTION_GATT_FIRST_SERVICES_DISCOVERED";
    //public final static String ACTION_GATT_SECOND_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SECOND_SERVICES_DISCOVERED";

    private static HashMap<String, String> attributes = new HashMap();
    public static String CONTROL_SWITCH = "0000fff1-0000-1000-8000-00805f9b34fb";
    public static String CONTROL_SAMP_FREQ = "0000fff5-0000-1000-8000-00805f9b34fb";
    public static String PRESSURE_SENSOR_MEASUREMENT = "0000fff2-0000-1000-8000-00805f9b34fb";
    public static String POWER_MEASUREMENT = "0000fff3-0000-1000-8000-00805f9b34fb";
    public static String HEART_BEAT = "0000fff4-0000-1000-8000-00805f9b34fb";
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public final static UUID UUID_PRESSURE_SENSOR_MEASUREMENT = UUID.fromString(SampleGattAttributes.PRESSURE_SENSOR_MEASUREMENT);
    public final static UUID UUID_POWER_MEASUREMENT = UUID.fromString(SampleGattAttributes.POWER_MEASUREMENT);

    static
    {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName)
    {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
