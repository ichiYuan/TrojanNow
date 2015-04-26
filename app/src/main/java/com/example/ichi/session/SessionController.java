package com.example.ichi.session;

import android.content.Context;

import java.net.*;
import java.util.List;

/**
 * This class implements CookieStore and will be initialize on
 * creation of the app, so that all HTTP requests are handled
 * by it automatically.
 *
 * Created by ichiYuan on 4/1/15.
 */
public class SessionController {

    // set default connections handled by itself
    public SessionController(Context context) {
        CookieHandler.setDefault( new CookieManager( new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL ) );
    }

}
