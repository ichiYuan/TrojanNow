package com.example.ichi.myapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ichi.clientcontroller.MyResultReceiver;
import com.example.ichi.servercomm.HTTPRequest;


public class ProfileActivity extends ActionBarActivity implements MyResultReceiver.Receiver {
    private boolean following = true;
    private int id = -1;
    private String name;

    private void setFollowing(boolean following) {
        this.following = following;
        sendRequestUsers();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle b = getIntent().getExtras();
        id = b.getInt("id");
        name = b.getString("name");

        TextView textView = (TextView) findViewById(R.id.user_name);
        textView.setText(name);

        Button button = (Button) findViewById(R.id.following_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setFollowing(true);
            }
        });
        button = (Button) findViewById(R.id.followers_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setFollowing(false);
            }
        });

        button = (Button) findViewById(R.id.send_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(v.getContext(), EditMsgActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name",name);
                v.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
                break;
            case ERROR:
                // handle the error;
                break;
        }
    }

    public void sendRequestUsers() {
        if (id < 0)
            return;
        String url = "https://rails-tutorial-cosimo-dw.c9.io/users/"+id+"/"+(following?"following":"followers")+".json";

        Intent intent = HTTPRequest.makeIntent(this, this, url, "GET", null);

        this.startService(intent);
    }
}
