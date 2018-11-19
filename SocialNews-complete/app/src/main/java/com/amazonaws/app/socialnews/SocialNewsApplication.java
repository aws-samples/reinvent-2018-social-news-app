package com.amazonaws.app.socialnews;

import android.app.Application;

public class SocialNewsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ClientFactory.init(getApplicationContext());
    }
}
