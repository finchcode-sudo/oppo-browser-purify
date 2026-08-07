package com.heytap.purify;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            com.google.android.material.color.DynamicColors.applyToActivitiesIfAvailable(this);
        } catch (Throwable ignored) {
        }
    }
}
