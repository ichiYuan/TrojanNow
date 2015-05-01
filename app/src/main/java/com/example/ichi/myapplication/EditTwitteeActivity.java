package com.example.ichi.myapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.ichi.clientcontroller.MyResultReceiver;
import com.example.ichi.servercomm.HTTPRequest;

import java.util.HashMap;
import java.util.Map;


public class EditTwitteeActivity extends ActionBarActivity implements MyResultReceiver.Receiver {
    EditText mText;
    CheckBox mAnonymous;
    CheckBox mSensor;
    Button mPostButton;
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
                sendMicropost(mText.getText().toString(),mAnonymous.isChecked(),mSensor.isChecked());
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
                finish();
                break;
            case ERROR:
                // handle the error;
                break;
        }
    }

    // send twit to server
    void sendMicropost(String content, Boolean anonymous, Boolean sensor) {
        String url = "https://rails-tutorial-cosimo-dw.c9.io/microposts/new.json";
        Map<String,String> params = new HashMap<String,String>();
        params.put("micropost[content]", content);
        params.put("micropost[anony]", anonymous?"1":"0");
        //TODO environment
        String environment = null;
        if (sensor) environment = "usc";
        params.put("micropost[environment]",environment);
        params.put("[remember_me]", "1");

        Intent intent = HTTPRequest.makeIntent(this, this, url, "POST", params);

        startService(intent);
    }
}
