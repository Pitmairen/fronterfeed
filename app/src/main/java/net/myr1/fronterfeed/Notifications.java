package net.myr1.fronterfeed;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * Broadcast receiver that used to create notifications
 */
public class Notifications extends BroadcastReceiver {

    private Context mContext;

    public Notifications(Context context)
    {
        mContext = context.getApplicationContext();

    }


    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent == null)
            return;


        final String status = intent.getStringExtra(FronterService.STATUS);

        if(status.equals(FronterService.STATUS_SUCCESS)){

            int newMessages = intent.getIntExtra(FronterService.STATUS_NEW_ITEMS, 0);


            if(newMessages > 0){
                notifyNewMessages();
            }



        }


    }

    private void notifyNewMessages()
    {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_stat_social_school)
                        .setContentTitle("Fronter Feed")
                        .setContentText(mContext.getString(R.string.notifications_message))
                        .setDefaults(Notification.DEFAULT_ALL);

        Intent resultIntent = new Intent(mContext, FeedActivity.class);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(FeedActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);


        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, builder.build());


    }



    public void removeActiveNotification(){
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(0);

    }


}
