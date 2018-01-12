package com.example.chongchenlearn901.instagramclone;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by chongchen on 2018-01-12.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this);
    }
}
