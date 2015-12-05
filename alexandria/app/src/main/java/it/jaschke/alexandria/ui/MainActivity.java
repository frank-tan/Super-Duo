package it.jaschke.alexandria.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.api.BookListItemCallback;
import it.jaschke.alexandria.util.Utilities;


public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, BookListItemCallback, BookDetailFragment.HomeIconButtonCallback {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment navigationDrawerFragment;

    /**
     * Used to store the last screen mTitle. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    public static boolean mIsTablet = false;
    private boolean mRightPaneVisible = false;
    private int mActiveScreenIndex = 0;
    private BroadcastReceiver mBroadcastReceiver;

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";

    private static final String ACTIVE_SCREEN = "ACTIVE_SCREEN";
    private static final String RIGHT_PANE_VISIBLE = "RIGHT_PANE_VISIBLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsTablet = isTablet();
        if(mIsTablet){
            setContentView(R.layout.activity_main_tablet);
        } else {
            setContentView(R.layout.activity_main);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBroadcastReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,filter);

        navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        navigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        if(savedInstanceState != null) {
            int lastActiveScreen = savedInstanceState.getInt(ACTIVE_SCREEN, -1);
            // if the last screen is recorded, let the system handle the restoration
            if (lastActiveScreen != -1) {
                mActiveScreenIndex = lastActiveScreen;
                return;
            }
        }

        // if the last screen is not recorded, open the page stored in user preference
        navigationDrawerFragment.selectItem(Integer.parseInt(Utilities.getStringPreference(this, getString(R.string.pref_startScreen), "0")));

        Log.i("alexandria", "mActiveScreenIndex: " + mActiveScreenIndex);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        // restore last active screen if available
        if(savedInstanceState != null) {
            mRightPaneVisible = savedInstanceState.getBoolean(RIGHT_PANE_VISIBLE, false);
        }

        if(mIsTablet) {
            if (mRightPaneVisible) {
                findViewById(R.id.right_container).setVisibility(View.VISIBLE);
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(ACTIVE_SCREEN, mActiveScreenIndex);
        if(mIsTablet) {
            outState.putBoolean(RIGHT_PANE_VISIBLE, findViewById(R.id.right_container).getVisibility() == View.VISIBLE);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment nextFragment;

        mActiveScreenIndex = position;

        switch (position){
            default:
            case 0:
                nextFragment = new ListOfBooksFragment();
                break;
            case 1:
                nextFragment = new AddBookFragment();
                if(mIsTablet)
                    findViewById(R.id.right_container).setVisibility(View.GONE);
                break;
            case 2:
                nextFragment = new AboutFragment();
                if(mIsTablet)
                    findViewById(R.id.right_container).setVisibility(View.GONE);
                break;
        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, nextFragment)
                .addToBackStack((String) mTitle)
                .commit();
    }

    @Override
    public void selectNavigationDrawerItem(int position) {
        navigationDrawerFragment.selectItem(position);
    }

    @Override
    public void setTitle(int titleId) {
        mTitle = getString(titleId);
        if(mIsTablet) {
            getSupportActionBar().setTitle(R.string.app_name);
        } else {
            getSupportActionBar().setTitle(mTitle);
        }
    }

    private void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        //added null check
        if(actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            setTitle(mTitle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String ean) {
        mActiveScreenIndex = 3;

        Bundle args = new Bundle();
        args.putString(BookDetailFragment.EAN_KEY, ean);

        BookDetailFragment fragment = new BookDetailFragment();
        fragment.setArguments(args);

        int id;
        if(mIsTablet) {
            id = R.id.right_container;
            findViewById(id).setVisibility(View.VISIBLE);
        } else {
            id = R.id.container;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(id, fragment)
                .addToBackStack(getString(R.string.details))
                .commit();
    }

    private void setActionBarIconAsUp () {
        navigationDrawerFragment.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setActionBarIconAsNav () {
        navigationDrawerFragment.setDrawerIndicatorEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(MESSAGE_KEY)!=null){
                Toast.makeText(MainActivity.this, intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onBackPressed() {
        setActionBarIconAsNav();

        if(getSupportFragmentManager().getBackStackEntryCount()<2){
            finish();
        }
        super.onBackPressed();
    }

    @Override
    public void setHomeIconAsUp() {
        setActionBarIconAsUp();
    }

    @Override
    public void setHomeIconAsNav() {
        setActionBarIconAsNav();
    }
}
