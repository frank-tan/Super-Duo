package barqsoft.footballscores.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.R;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class PagerFragment extends Fragment
{
    private static int mCurrentFragmentId = 2;

    private static String CURRENT_FRAGMENT_ID_TAG = "PAGER_CURRENT_FRAGMENT_TAG";

    private ViewPager mViewPager;
    private TabPagerAdapter mPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);

        if (savedInstanceState != null) {
            mCurrentFragmentId = savedInstanceState.getInt(CURRENT_FRAGMENT_ID_TAG);
        }

        mPagerAdapter = new TabPagerAdapter(getChildFragmentManager());

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mCurrentFragmentId);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_FRAGMENT_ID_TAG, mViewPager.getCurrentItem());
    }

    private class TabPagerAdapter extends FragmentStatePagerAdapter
    {
        private static final int NUM_PAGES = 5;

        public TabPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int i)
        {
            Date fragmentDate = new Date(System.currentTimeMillis()+((i-2)*86400000));
            SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
            return MatchListFragment.newInstance(mFormat.format(fragmentDate));
        }

        @Override
        public int getCount()
        {
            return NUM_PAGES;
        }

        // Returns the page title for the top indicator
        // // TODO: 31/10/2015 remove the deprecated class
        @Override
        public CharSequence getPageTitle(int position)
        {
            return getDayName(getActivity(),System.currentTimeMillis()+((position-2)*86400000));
        }
        private String getDayName(Context context, long dateInMillis) {
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.

            Time t = new Time();
            t.setToNow();
            int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
            if (julianDay == currentJulianDay) {
                return context.getString(R.string.today);
            } else if ( julianDay == currentJulianDay +1 ) {
                return context.getString(R.string.tomorrow);
            }
             else if ( julianDay == currentJulianDay -1)
            {
                return context.getString(R.string.yesterday);
            }
            else
            {
                // Otherwise, the format is just the day of the week (e.g "Wednesday".
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                return dayFormat.format(dateInMillis);
            }
        }
    }
}
