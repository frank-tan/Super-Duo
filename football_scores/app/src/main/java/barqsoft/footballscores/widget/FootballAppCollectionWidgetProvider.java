package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.RemoteViews;
import android.widget.Toast;

import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.ui.MainActivity;

/**
 * Created by tan on 4/11/2015.
 */
public class FootballAppCollectionWidgetProvider extends AppWidgetProvider {
    private static String CLICK_ACTION = "barqsoft.footballscores.widget.LIST_CLICK";
//    public static String REFRESH_ACTION = "barqsoft.footballscores.widget.LIST_REFRESH";

    private static Handler sWorkerQueue;
    private static ScoreDataProviderObserver sDataObserver;

    public FootballAppCollectionWidgetProvider() {
        HandlerThread sWorkerThread = new HandlerThread("FootballAppCollectionWidgetProvider-worker");
        sWorkerThread.start();
        sWorkerQueue = new Handler(sWorkerThread.getLooper());
    }

    @Override
    public void onEnabled(Context context) {
        // Register for external updates to the data to trigger an update of the widget.  When using
        // content providers, the data is often updated via a background service, or in response to
        // user interaction in the main app.  To ensure that the widget always reflects the current
        // state of the data, we must listen for changes and update ourselves accordingly.
        final ContentResolver r = context.getContentResolver();
        if (sDataObserver == null) {
            final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            final ComponentName cn = new ComponentName(context, FootballAppCollectionWidgetProvider.class);
            sDataObserver = new ScoreDataProviderObserver(mgr, cn, sWorkerQueue);
            r.registerContentObserver(DatabaseContract.scores_table.buildScoreWithDate(), true, sDataObserver);
        }
        Toast.makeText(context, "widget enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        final String action = intent.getAction();

        if (action.equals(CLICK_ACTION)) {
            Toast.makeText(ctx, "widget clicked", Toast.LENGTH_SHORT).show();
            // Show a toast
//            final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
//                    AppWidgetManager.INVALID_APPWIDGET_ID);
            intent.setClass(ctx, MainActivity.class);
            ctx.startActivity(intent);
        }

        super.onReceive(ctx, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Toast.makeText(context, "widget onupdate", Toast.LENGTH_SHORT).show();
        // Update each of the widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {
            // Specify the service to provide data for the collection widget.  Note that we need to
            // embed the appWidgetId via the data otherwise it will be ignored.
            final Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            final RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_match_list);
            if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                rv.setRemoteAdapter(R.id.scores_list, intent);
            } else {
                rv.setRemoteAdapter(appWidgetIds[i], R.id.scores_list, intent);
            }

            // Set the empty view to be displayed if the collection is empty.  It must be a sibling
            // view of the collection view.
            rv.setEmptyView(R.id.scores_list, R.id.empty_view);

            // Bind a click listener template for the contents of the weather list.  Note that we
            // need to update the intent's data if we set an extra, since the extras will be
            // ignored otherwise.
            final Intent onClickIntentTemplate = new Intent(context, FootballAppCollectionWidgetProvider.class);
            onClickIntentTemplate.setAction(FootballAppCollectionWidgetProvider.CLICK_ACTION);
            onClickIntentTemplate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            onClickIntentTemplate.setData(Uri.parse(onClickIntentTemplate.toUri(Intent.URI_INTENT_SCHEME)));
            final PendingIntent onClickPendingIntentTemplate = PendingIntent.getBroadcast(context, 0,
                    onClickIntentTemplate, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.scores_list, onClickPendingIntentTemplate);

            // Create an Intent to launch ExampleActivity
            Intent onClickIntent = new Intent(context, MainActivity.class);
            PendingIntent onClickPendingIntent = PendingIntent.getActivity(context, 0, onClickIntent, 0);

            rv.setOnClickPendingIntent(R.id.widget_title, onClickPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

}

class ScoreDataProviderObserver extends ContentObserver {
    private AppWidgetManager mAppWidgetManager;
    private ComponentName mComponentName;

    ScoreDataProviderObserver(AppWidgetManager mgr, ComponentName cn, Handler h) {
        super(h);
        mAppWidgetManager = mgr;
        mComponentName = cn;
    }

    @Override
    public void onChange(boolean selfChange) {
        // The data has changed, so notify the widget that the collection view needs to be updated.
        // In response, the factory's onDataSetChanged() will be called which will requery the
        // cursor for the new data.
        mAppWidgetManager.notifyAppWidgetViewDataChanged(
                mAppWidgetManager.getAppWidgetIds(mComponentName), R.id.scores_list);
    }
}
