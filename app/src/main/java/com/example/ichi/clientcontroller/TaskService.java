package com.example.ichi.clientcontroller;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Pair;

import com.example.ichi.servercomm.CollectionUtils;
import com.example.ichi.servercomm.HTTPRequest;
import com.example.ichi.session.SessionController;

import java.util.ArrayList;
import java.util.Map;

/**
 * This is the Client Controller. Android's IntentService
 * provides exactly same functionality as an event based
 * FIFO controller. UIs send intents to this service (a
 * different thread), and it will inform result back when
 * it finishes the task. It may ask HTTPRequest (Server
 * Communication) to retrieve data or obtain Sensors'
 * information, etc.
 *
 * Created by ichiYuan on 3/31/15.
 */
public class TaskService extends IntentService {
    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_FINISHED = 0;
    public static final int STATUS_ERROR = -1;

    public TaskService() {
        super("TaskService");
    }

    public void onCreate(){
        super.onCreate();
        new SessionController(getApplicationContext());
    }

    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String command = intent.getStringExtra("command");
        Bundle b = new Bundle();
        if (command.equals("query")) {
            // get data via HTTP
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);
            try {
                HTTPRequest req = new HTTPRequest();
                ArrayList<String> results = req.request(intent.getStringExtra("url"),
                        intent.getStringExtra("method"),
                        CollectionUtils.fromBundle(intent.getBundleExtra("params")));
                b.putStringArrayList("results", results);
                receiver.send(STATUS_FINISHED, b);
            } catch (Exception e) {
                b.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, b);
            }
        }
        if (command.equals("sensor")) {
            // get sensors' data
        }
    }
}

