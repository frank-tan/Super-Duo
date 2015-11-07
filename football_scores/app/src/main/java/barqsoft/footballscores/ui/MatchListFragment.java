package barqsoft.footballscores.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import barqsoft.footballscores.R;
import barqsoft.footballscores.adapter.ScoresAdapter;
import barqsoft.footballscores.adapter.ViewHolder;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.service.myFetchService;

/**
 * A placeholder fragment containing a simple view.
 */
public class MatchListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static int mSelectedMatchId;
    private ScoresAdapter mAdapter;
    private String mFragmentDate;
    private int last_selected_item = -1;

    private static final String SELECTED_MATCH_ID_TAG = "SELECTED_MATCH_ID_TAG";
    private static final String FRAGMENT_DATE_TAG = "FRAGMENT_DATE_TAG";
    private static final int SCORES_LOADER = 0;

    public MatchListFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            mSelectedMatchId = savedInstanceState.getInt(SELECTED_MATCH_ID_TAG);
            mFragmentDate = savedInstanceState.getString(FRAGMENT_DATE_TAG);
        }
        Log.v("MatchListFragment", "mFragmentDate is " + mFragmentDate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // TODO: 31/10/2015 should not do update here
        update_scores();

        View rootView = inflater.inflate(R.layout.fragment_match_list, container, false);
        final ListView score_list = (ListView) rootView.findViewById(R.id.scores_list);

        mAdapter = new ScoresAdapter(getActivity(),null,0);
        mAdapter.setDetailMatchId(mSelectedMatchId);

        score_list.setAdapter(mAdapter);
        score_list.setEmptyView(rootView.findViewById(R.id.empty_view));

        score_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder selectedView = (ViewHolder) view.getTag();
                mAdapter.setDetailMatchId(selectedView.matchId);

                mSelectedMatchId = (int) selectedView.matchId;
                mAdapter.notifyDataSetChanged();
            }
        });
        getLoaderManager().initLoader(SCORES_LOADER, null, this);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_MATCH_ID_TAG, mSelectedMatchId);
        outState.putString(FRAGMENT_DATE_TAG,mFragmentDate);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        String[] selectionArgs = new String[]{mFragmentDate};
        if(selectionArgs == null) {
            Log.e("MatchListFragment","selectionArgs is null");
        }
        return new CursorLoader(getActivity(), DatabaseContract.scores_table.buildScoreWithDate(),
                null,null, selectionArgs,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        mAdapter.swapCursor(null);
    }

    // TODO: 31/10/2015 does not belong here
    private void update_scores()
    {
        Intent intent = new Intent(getActivity(), myFetchService.class);
        getActivity().startService(intent);
    }
    public void setFragmentDate(String date)
    {
        mFragmentDate = date;
    }

    public static Fragment newInstance(String dateString) {
        MatchListFragment matchListFragment = new MatchListFragment();
        matchListFragment.setFragmentDate(dateString);
        return matchListFragment;
    }
}
