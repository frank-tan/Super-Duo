package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.data.DatabaseContract;

/**
 * Created by tan on 3/11/2015.
 */
public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ScoreListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class ScoreListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private Cursor mCursor;
    private int mAppWidgetId;

    private static final int COL_HOME = 3;
    private static final int COL_AWAY = 4;
    private static final int COL_HOME_GOALS = 6;
    private static final int COL_AWAY_GOALS = 7;
    private static final int COL_DATE = 1;
    private static final int COL_LEAGUE = 5;
    private static final int COL_MATCHDAY = 9;
    private static final int COL_ID = 8;
    private static final int COL_MATCHTIME = 2;

    public ScoreListRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public int getCount() {
        if (mCursor != null) return mCursor.getCount();
        return 0;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        // Refresh the cursor
        if (mCursor != null) {
            mCursor.close();
        }

        Date fragmentDate = new Date(System.currentTimeMillis());
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] selectionArgs = new String[]{mFormat.format(fragmentDate)};

        final long token = Binder.clearCallingIdentity();
        try {
            mCursor = mContext.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
                    null,null, selectionArgs,null);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) mCursor.close();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        // Get the data for this position from the content provider

        if (mCursor.moveToPosition(position)) {
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.scores_list_item);
            rv.setTextViewText(R.id.home_name,mCursor.getString(COL_HOME));
            rv.setTextViewText(R.id.away_name,mCursor.getString(COL_AWAY));
            rv.setTextViewText(R.id.data_textview,mCursor.getString(COL_MATCHTIME));
            rv.setTextViewText(R.id.score_textview, Utilies.getScores(mCursor.getInt(COL_HOME_GOALS), mCursor.getInt(COL_AWAY_GOALS)));
            rv.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(
                    mCursor.getString(COL_HOME)));
            rv.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(
                    mCursor.getString(COL_AWAY)));

            // Set the click intent so that we can handle it and show a toast message
            final Intent fillInIntent = new Intent();
            rv.setOnClickFillInIntent(R.id.widget, fillInIntent);

            return rv;
        }

        return null;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}