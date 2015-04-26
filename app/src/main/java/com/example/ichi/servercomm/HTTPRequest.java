package com.example.ichi.servercomm;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.*;

import com.example.ichi.clientcontroller.MyResultReceiver;
import com.example.ichi.clientcontroller.TaskService;

import java.util.*;
import java.io.*;
import java.net.*;

import javax.net.ssl.HttpsURLConnection;

/**
 * This is the Server Communication part. Being invoked by
 * TaskService (Client Controller), it sends HTTP request
 * with help of SessionController and return String as its
 * result. (It's designed to use JSON format.)
 *
 * Created by ichiYuan on 3/31/15.
 */
public class HTTPRequest {
    private static final String DEBUG_TAG = "Debug:\t";

    static public Intent makeIntent(Context context, MyResultReceiver.Receiver receiver, String url, String method, Map<String, String> params) {
        Intent intent = new Intent();
        intent.setClass(context, TaskService.class);
        intent.putExtra("command", "query");
        intent.putExtra("url",url);
        intent.putExtra("method", method);
        if (params == null)
            params = new HashMap<String, String>();
        intent.putExtra("params", CollectionUtils.toBundle(params));

        MyResultReceiver mReceiver = new MyResultReceiver(new Handler());
        mReceiver.setReceiver(receiver);
        intent.putExtra("receiver",mReceiver);
        return intent;
    }

    public ArrayList<String> request(String myURL, String method, Map<String,String> params) throws IOException {
        InputStream is = null;
        // Only display the first 5000 characters of the retrieved
        // web page content.
        int len = 5000;

        try {
            if (method.equals("GET") && !params.isEmpty()) {
                myURL = myURL + "&" + getPostDataString(params);
            }
            URL url = new URL(myURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            if (method.equals("POST") || method.equals("PUT")) {
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(params));
                writer.flush();
                writer.close();
                os.close();
            }

            conn.connect();
            int responseCode=conn.getResponseCode();
            Log.d(DEBUG_TAG, "The responseCode is: " + responseCode);

            if (responseCode != HttpsURLConnection.HTTP_OK)
                return null;

            is = conn.getInputStream();

            // Convert the InputStream into a string
            ArrayList<String> contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private String getPostDataString(Map<String,String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    // Reads an InputStream and converts it to a String.
    private ArrayList<String> readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        ArrayList<String> ret = new ArrayList<String>();
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        BufferedReader br = new BufferedReader(reader);

        String s;
        while ((s = br.readLine()) != null) {
            ret.add(s);
        }
        reader.close();
        return ret;
    }
}
