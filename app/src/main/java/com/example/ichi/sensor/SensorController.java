package com.example.ichi.sensor;

import android.content.Context;
import android.content.Intent;
import android.hardware.*;
import android.os.Handler;

import com.example.ichi.clientcontroller.MyResultReceiver;
import com.example.ichi.clientcontroller.TaskService;

/**
 * Sensor Controller. In Android sensors are attached to a
 * certain activity's content, so the activity itself has
 * to call Sensor Controller and then pass the data to
 * Client Controller.
 *
 * Created by ichiYuan on 4/1/15.
 */
public class SensorController implements SensorEventListener{
    private Sensor mTemp;
    private float temperature;
    static public Intent makeIntent(Context context, MyResultReceiver.Receiver receiver) {
        Intent intent = new Intent();
        intent.setClass(context, TaskService.class);
        intent.putExtra("command", "sensor");

        MyResultReceiver mReceiver = new MyResultReceiver(new Handler());
        mReceiver.setReceiver(receiver);
        intent.putExtra("receiver",mReceiver);
        return intent;
    }
    // manager needs to be obtained by activity, so when an
    // activity wants a sensor data, it initialize this
    // controller by itself.
    public SensorController(SensorManager manager) {
        mTemp = manager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        // should add listener here
        manager.registerListener(this,mTemp,manager.SENSOR_DELAY_NORMAL);
    }

    public float getTemperature() {
        return temperature;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // should modify intent and send it to TaskService
        if (event.sensor.getType() != Sensor.TYPE_AMBIENT_TEMPERATURE)
            return;
        temperature = event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
}
