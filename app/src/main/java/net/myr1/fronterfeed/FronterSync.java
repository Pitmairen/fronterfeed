package net.myr1.fronterfeed;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Schedules repeating auto downloads of the feed.
 */
public class FronterSync {


    private AlarmManager mAlarmManager;
    private Context mContext;

    public FronterSync(Context context) {
        mContext = context.getApplicationContext();
        mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    }


    /**
     * Schedule repeating downloads of the feed at the specified interval.
     * @param interval how often to updated in millis.
     */
    public void scheduleUpdates(long interval)
    {
        scheduleUpdates(interval, interval);

    }

    /**
     * Schedule repeating downloads of the feed at the specified interval.
     * @param firstRun when to run the first update in millis.
     * @param interval how often to updated in millis.
     */
    public void scheduleUpdates(long firstRun, long interval)
    {
        PendingIntent updateIntent = createSyncIntent();

        mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + firstRun, interval, updateIntent);

    }


    /**
     * Disables the auto sync updates.
     */
    public void disableSync()
    {
        PendingIntent updateIntent = createSyncIntent();

        mAlarmManager.cancel(updateIntent);
        updateIntent.cancel();
    }



    private PendingIntent createSyncIntent()
    {

        Intent intent = new Intent(mContext, FronterService.class);
        intent.setAction(FronterService.ACTION_DOWNLOAD_FEED);

        return PendingIntent.getService(mContext, 0, intent, 0);

    }

}
