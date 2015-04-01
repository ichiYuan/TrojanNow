package com.example.ichi.sensor;

import android.content.Intent;
import android.hardware.*;

/**
 * Sensor Controller. In Android sensors are attached to a
 * certain activity's content, so the activity itself has
 * to call Sensor Controller and then pass the data to
 * Client Controller.
 *
 * Created by ichiYuan on 4/1/15.
 */
public class SensorController implements SensorEventListener{
    private Sensor mPressure;
    private Intent intent;

    // manager needs to be obtained by activity, so when an
    // activity wants a sensor data, it initialize this
    // controller by itself.
    public SensorController(SensorManager manager, Intent intent) {
        mPressure = manager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        // should add listener here
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // should modify intent and send it to TaskService
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
}
