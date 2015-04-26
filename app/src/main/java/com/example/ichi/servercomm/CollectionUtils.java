package com.example.ichi.servercomm;

/**
 * Created by cosimodw on 4/22/15.
 */

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.os.Parcelable;

public class CollectionUtils {

    public static Bundle toBundle(Map<String, String> input) {
        Bundle output = new Bundle();
        for(String key : input.keySet()) {
            output.putString(key, input.get(key));
        }
        return output;
    }

    public static  Map<String, String> fromBundle(Bundle input) {
        Map<String, String> output = new HashMap<String, String>();
        for(String key : input.keySet()) {
            output.put(key, input.getString(key));
        }
        return output;
    }
}