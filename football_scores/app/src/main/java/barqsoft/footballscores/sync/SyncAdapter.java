package barqsoft.footballscores.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;

/**
 * Created by tan on 5/12/2015.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String APP_NAME = "football-scores";
    // Sync every 1 hours
    private static final long SYNC_INTERVAL = (long) 1 * 60 * 60;

    // With .5 hours flexible time
    private static final long SYNC_FLEXTIME = (long) .5 * 60 * 60;

    private ContentResolver mContentResolver;

    /**
     * Create or get existing sync account and schedule a periodic sync
     * @param context
     */
    public static void initialize(Context context) {
        Log.i(APP_NAME, "initializing sync adapter");

        Account account = getSyncAccount(context);

        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        Context.ACCOUNT_SERVICE);

        // if the account type does not exist, add a new account
        if (accountManager.getAccountsByType(context.getString(R.string.sync_account_type)).length == 0) {
            Log.i(APP_NAME,"account does not exist. Add new account ");
            /*
             * Add the account and account type, no password or user data
             * If successful, return the Account object, otherwise report an error.
             */
            if (!accountManager.addAccountExplicitly(account, null, null)) {
                /*
                 * If you don't set android:syncable="true" in
                 * in your <provider> element in the manifest,
                 * then call context.setIsSyncable(account, AUTHORITY, 1)
                 * here.
                 */
                Log.i(APP_NAME,"failed to add new account");
            }
            //account added successfully, set periodical sync
            Log.i(APP_NAME, "add new account successfully");
            setPeriodicSync(context, account);
            syncNow(context);
        }
    }

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        mContentResolver = context.getContentResolver();
    }

    /**
     * The logic for performing sync
     * @param account
     * @param extras
     * @param authority
     * @param provider
     * @param syncResult
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.i(APP_NAME, "to call fetch data");
        SyncExecutor.getData("n2", getContext().getApplicationContext());
        SyncExecutor.getData("p2", getContext().getApplicationContext());
        Log.i(APP_NAME, "fetch data called");

    }

    /**
     * Do a synchronise from moviedb now
     * @param context
     */
    public static void syncNow (Context context) {
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        Log.i(APP_NAME, "Request sync");
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), settingsBundle);
        Log.i(APP_NAME, "sync requested");
    }

    /**
     * Create an account for sync adapter
     * @param context
     * @return
     */
    private static Account getSyncAccount(Context context) {
        // Create the account type and default account
        return new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
    }

    /**
     * Set synchronisation everyday with 4 hours flexible time
     * @param context
     * @param account
     */
    private static void setPeriodicSync(Context context, Account account) {
        String authority = context.getResources().getString(R.string.content_authority);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(SYNC_INTERVAL, SYNC_FLEXTIME).
                    setSyncAdapter(account, authority).
                    setExtras(Bundle.EMPTY).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(
                    account,
                    authority,
                    Bundle.EMPTY,
                    SYNC_INTERVAL);
        }

        ContentResolver.setSyncAutomatically(account, context.getString(R.string.content_authority), true);
    }

}
