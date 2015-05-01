package com.example.ichi.myapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ichi.clientcontroller.MyResultReceiver;
import com.example.ichi.servercomm.HTTPRequest;

import java.util.HashMap;
import java.util.Map;


public class EditMsgActivity extends ActionBarActivity implements MyResultReceiver.Receiver {
    private int receiverID;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_msg);
        Bundle b = getIntent().getExtras();
        receiverID = b.getInt("id");
        name = b.getString("name");

        TextView textView = (TextView) findViewById(R.id.receiver_name);
        textView.setText(name);

        final EditText editText = (EditText) findViewById(R.id.message);

        Button button = (Button) findViewById(R.id.post_message);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(editText.getText().toString());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_msg, menu);
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

    // send message to server
    void sendMessage(String content) {
        String url = "https://rails-tutorial-cosimo-dw.c9.io/messages.json";
        Map<String,String> params = new HashMap<String,String>();
        params.put("message[content]", content);
        params.put("message[receiver_id]",String.valueOf(receiverID));

        Intent intent = HTTPRequest.makeIntent(this, this, url, "POST", params);

        startService(intent);
    }
}
