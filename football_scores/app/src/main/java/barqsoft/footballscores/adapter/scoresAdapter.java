package barqsoft.footballscores.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorAdapter
{
    private static final int COL_HOME = 3;
    private static final int COL_AWAY = 4;
    private static final int COL_HOME_GOALS = 6;
    private static final int COL_AWAY_GOALS = 7;
    private static final int COL_DATE = 1;
    private static final int COL_LEAGUE = 5;
    private static final int COL_MATCHDAY = 9;
    private static final int COL_ID = 8;
    private static final int COL_MATCHTIME = 2;
    private static final String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";

    private int detailMatchId = 0;

    public ScoresAdapter(Context context, Cursor cursor, int flags)
    {
        super(context,cursor,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        itemView.setTag(holder);
        return itemView;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.homeName.setText(cursor.getString(COL_HOME));
        viewHolder.awayName.setText(cursor.getString(COL_AWAY));
        viewHolder.date.setText(cursor.getString(COL_MATCHTIME));
        viewHolder.score.setText(Utilities.getScores(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS)));
        viewHolder.matchId = (int) cursor.getDouble(COL_ID);
        viewHolder.homeCrest.setImageResource(Utilities.getTeamCrestByTeamName(
                cursor.getString(COL_HOME)));
        viewHolder.awayCrest.setImageResource(Utilities.getTeamCrestByTeamName(
                cursor.getString(COL_AWAY)));

        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = viewHolder.frameLayout;
        if(viewHolder.matchId == detailMatchId) {
            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
            match_day.setText(Utilities.getMatchDay(cursor.getInt(COL_MATCHDAY),
                    cursor.getInt(COL_LEAGUE)));
            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utilities.getLeague(cursor.getInt(COL_LEAGUE)));
            Button share_button = (Button) v.findViewById(R.id.share_button);
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    //add Share Action
                    context.startActivity(createShareForecastIntent(viewHolder.homeName.getText()+" "
                    +viewHolder.score.getText()+" "+viewHolder.awayName.getText() + " "));
                }
            });
        } else {
            container.removeAllViews();
        }

    }
    private Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        } else {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

    public void setDetailMatchId(int detailMatchId) {
        this.detailMatchId = detailMatchId;
    }
}
