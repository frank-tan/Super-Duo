package it.jaschke.alexandria;

import android.app.Application;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by tan on 24/10/2015.
 */
public class AlexandriaApplication extends Application {
    @Override public void onCreate() {
        super.onCreate();

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        if(BuildConfig.DEBUG) {
            built.setLoggingEnabled(true);
            //LeakCanary.install(this);
        }
        Picasso.setSingletonInstance(built);
    }
}
