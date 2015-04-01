package com.example.ichi.session;

import java.net.*;
import java.util.List;

/**
 * This class implements CookieStore and will be initialize on
 * creation of the app, so that all HTTP requests are handled
 * by it automatically.
 *
 * Created by ichiYuan on 4/1/15.
 */
public class SessionController implements CookieStore {

    // set default connections handled by itself
    public SessionController() {
        CookieManager cookieManager = new CookieManager(this,null);
        CookieHandler.setDefault(cookieManager);
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {

    }

    @Override
    public List<HttpCookie> get(URI uri) {
        return null;
    }

    @Override
    public List<HttpCookie> getCookies() {
        return null;
    }

    @Override
    public List<URI> getURIs() {
        return null;
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        return false;
    }

    @Override
    public boolean removeAll() {
        return false;
    }

    // Need to implement I/O to persistent storage
    private void readFromStore() {}
    private void writeToStore() {}
}
