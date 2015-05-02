package com.example.ichi.myapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.example.ichi.clientcontroller.MyResultReceiver;
import com.example.ichi.sensor.SensorController;
import com.example.ichi.servercomm.HTTPRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class EditTwitteeActivity extends ActionBarActivity implements MyResultReceiver.Receiver {
    EditText mText;
    CheckBox mAnonymous;
    CheckBox mSensor;
    Button mPostButton;
    private String sensorData = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_twittee);
        mText = (EditText) findViewById(R.id.twittee);
        mAnonymous = (CheckBox) findViewById(R.id.anonymous);
        mSensor = (CheckBox) findViewById(R.id.temperature);
        mPostButton = (Button) findViewById(R.id.post_twittee);
        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMicropost(mText.getText().toString(), mAnonymous.isChecked(), mSensor.isChecked());
            }
        });

        mSensor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sendSensorRequest();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_twittee, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case RUNNING:
                //show progress
                break;
            case FINISHED:
                // do something interesting
                // hide progress
                String sensor = resultData.getString("sensors");
                if (sensor != null) {
                    sensorData = sensor + "°C";
                    Log.d("DEBUG:",sensorData);
                }
                else {
                    finish();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
                break;
            case ERROR:
                // handle the error;
                break;
        }
    }

    // send twit to server
    void sendMicropost(String content, Boolean anonymous, Boolean sensor) {
        String url = "https://rails-tutorial-cosimo-dw.c9.io/microposts.json";
        Map<String,String> params = new HashMap<String,String>();
        params.put("micropost[content]", content);
        params.put("micropost[anony]", anonymous?"1":"0");
        if (sensor)
            params.put("micropost[environment]",sensorData);

        Intent intent = HTTPRequest.makeIntent(this, this, url, "POST", params);

        startService(intent);
    }

    void sendSensorRequest() {
        Intent intent = SensorController.makeIntent(this,this);
        startService(intent);
    }
}
