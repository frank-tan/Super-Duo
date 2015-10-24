package it.jaschke.alexandria.ui;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by tan on 24/10/2015.
 */
public class AlexandriaApplication extends Application {
    @Override public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
