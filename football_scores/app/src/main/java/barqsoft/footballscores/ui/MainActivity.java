package barqsoft.footballscores.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import barqsoft.footballscores.R;
import barqsoft.footballscores.sync.SyncAdapter;

public class MainActivity extends AppCompatActivity
{
    private PagerFragment mPagerFragment;
    private static String PAGER_FRAGMENT_TAG = "PAGER_FRAGMENT_TAG";

    private static String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SyncAdapter.initialize(this);

        if (savedInstanceState != null) {
            Log.v(LOG_TAG,"savedInstanceState is not null");
            // restore the previous state
            mPagerFragment = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState, PAGER_FRAGMENT_TAG);
            if(mPagerFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, mPagerFragment, PAGER_FRAGMENT_TAG)
                        .commit();
            }
        }

        // create pager fragment if it is null
        if(savedInstanceState == null) {
            Log.v(LOG_TAG,"fragment is null");
            mPagerFragment = new PagerFragment();

            // add pager fragment
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mPagerFragment, PAGER_FRAGMENT_TAG)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about)
        {
            Intent aboutIntent = new Intent(this,AboutActivity.class);
            startActivity(aboutIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, PAGER_FRAGMENT_TAG, mPagerFragment);
    }

}
