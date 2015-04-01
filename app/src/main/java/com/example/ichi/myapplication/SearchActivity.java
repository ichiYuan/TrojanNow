package com.example.ichi.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import java.util.*;

import com.example.ichi.clientcontroller.MyResultReceiver;


public class SearchActivity extends ActionBarActivity implements MyResultReceiver.Receiver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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

    // send request to get all followings
    void sendRequestFriendList() {

    }

    // send search params from input box to the server
    void sendSearchParams(List<Pair<String,String>> params) {

    }

    // send follow request to the server
    void sendFollow(String userID) {

    }
}
