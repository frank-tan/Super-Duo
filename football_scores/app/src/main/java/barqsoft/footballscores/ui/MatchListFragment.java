package barqsoft.footballscores.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import barqsoft.footballscores.R;
import barqsoft.footballscores.adapter.ViewHolder;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.adapter.scoresAdapter;
import barqsoft.footballscores.service.myFetchService;

/**
 * A placeholder fragment containing a simple view.
 */
public class MatchListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static int mSelectedMatchId;
    private scoresAdapter mAdapter;
    private String mFragmentDate;
    private int last_selected_item = -1;

    private static final String SELECTED_MATCH_ID_TAG = "SELECTED_MATCH_TAG";
    private static final int SCORES_LOADER = 0;

    public MatchListFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // TODO: 31/10/2015 should not do update here
        update_scores();
        getLoaderManager().initLoader(SCORES_LOADER, null, this);

        View rootView = inflater.inflate(R.layout.fragment_match_list, container, false);
        final ListView score_list = (ListView) rootView.findViewById(R.id.scores_list);

        if(savedInstanceState != null) {
            mSelectedMatchId = savedInstanceState.getInt(SELECTED_MATCH_ID_TAG);
        }

        mAdapter = new scoresAdapter(getActivity(),null,0);
        // TODO: 31/10/2015  should not set variable like this
        mAdapter.detail_match_id = mSelectedMatchId;
        score_list.setAdapter(mAdapter);

        score_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder selected = (ViewHolder) view.getTag();
                // TODO: 31/10/2015 should not set variable like this
                mAdapter.detail_match_id = selected.match_id;
                mSelectedMatchId = (int) selected.match_id;
                mAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_MATCH_ID_TAG, mSelectedMatchId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(getActivity(), DatabaseContract.scores_table.buildScoreWithDate(),
                null,null, new String[] {mFragmentDate} ,null);
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
