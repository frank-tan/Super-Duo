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
//        String city = "Unknown City";
//        int temp = 0;
//        if (mCursor.moveToPosition(position)) {
//            final int cityColIndex = mCursor.getColumnIndex(WeatherDataProvider.Columns.CITY);
//            final int tempColIndex = mCursor.getColumnIndex(
//                    WeatherDataProvider.Columns.TEMPERATURE);
//            city = mCursor.getString(cityColIndex);
//            temp = mCursor.getInt(tempColIndex);
//        }

        // Return a proper item with the proper city and temperature.  Just for fun, we alternate
        // the items to make the list easier to read.

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.scores_list_item);
        rv.setTextViewText(R.id.home_name,"Frank");
        rv.setTextViewText(R.id.away_name,"Ariel");
        rv.setImageViewResource(R.id.home_crest, R.drawable.ic_launcher);
        rv.setImageViewResource(R.id.away_crest, R.drawable.ic_launcher);

        // Set the click intent so that we can handle it and show a toast message
        final Intent fillInIntent = new Intent();
        rv.setOnClickFillInIntent(R.id.widget, fillInIntent);

        return rv;
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