package com.example.ichi.servercomm;

import android.util.*;

import java.util.*;
import java.io.*;
import java.net.*;

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
    public String request(String myURL, String method, List<Pair<String,String>> params) throws IOException {
        InputStream is = null;
        // Only display the first 5000 characters of the retrieved
        // web page content.
        int len = 5000;

        try {
            URL url = new URL(myURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
