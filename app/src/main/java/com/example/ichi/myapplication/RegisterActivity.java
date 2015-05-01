package com.example.ichi.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ichi.clientcontroller.MyResultReceiver;
import com.example.ichi.clientcontroller.TaskService;
import com.example.ichi.servercomm.CollectionUtils;
import com.example.ichi.servercomm.HTTPRequest;
import com.example.ichi.session.SessionController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends ActionBarActivity implements MyResultReceiver.Receiver{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
                ArrayList<String> results = resultData.getStringArrayList("results");
                if (results != null) {
                    Log.d("Debug:\t",results.get(0));
                    if (results.get(0).startsWith("INVALID")) {
                        Log.d("Debug:\t",results.get(0));
                        //TODO
                    }
                    else {

                        finish();
                    }
                }
                //showProgress(false);

                // hide progress
                break;
            case ERROR:
                // handle the error;
                break;
        }
    }
    // send registration info to the server

    void sendRegistration(String email, String password, String first_name, String last_name, String user_name) {
        String url = "https://rails-tutorial-cosimo-dw.c9.io/users/new.json";
        Map<String, String> params = new HashMap<String, String>();
        params.put("user[name]",user_name);
        params.put("user[first_name]",first_name);
        params.put("user[last_name]",last_name);
        params.put("user[email]",email);
        params.put("user[password]", password);
        params.put("user[password_confirmation]", password);

        Intent intent = HTTPRequest.makeIntent(this, this, url, "POST", params);
        startService(intent);
    }
}
