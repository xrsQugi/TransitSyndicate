package com.transitsyndicate;

import android.app.Application;

import com.transitsyndicate.core.di.AppContainer;

import org.osmdroid.config.Configuration;

import java.io.File;

public class TransitSyndicateApp extends Application {

    public AppContainer container;

    @Override
    public void onCreate() {
        super.onCreate();
        container = new AppContainer(this);

        Configuration.getInstance().setUserAgentValue(getPackageName());
        Configuration.getInstance().setOsmdroidTileCache(
                new File(getCacheDir(), "osmdroid")
        );
    }
}
