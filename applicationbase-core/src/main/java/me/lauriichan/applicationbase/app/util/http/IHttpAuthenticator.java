package me.lauriichan.applicationbase.app.util.http;

import java.net.HttpURLConnection;

@FunctionalInterface
public interface IHttpAuthenticator {
    
    void authenticate(HttpURLConnection connection);

}
