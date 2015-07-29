package net.myr1.fronterfeed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Re enable notifications/sync after having no network.
 * If a sync fails while no network is available this receiver is enabled and will
 * re enable sync when the device gets connected to the internet.
 */
public class ConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {

            Settings s = new Settings(context);

            if (!s.notificationsEnabled() || !s.hasWorkingConfig()) {
                // If notifications has been disabled since the receiver was enabled
                // we just disable it.
                Connectivity.disableConnectivityReceviver(context);


            } else if (Features.getInstance().canAutoSync()) {

                // Disabled it. It will be re enabled when needed.
                Connectivity.disableConnectivityReceviver(context);

                // Re enable notifications
                Features.getInstance().enableNotifications();

                // Update the data if the data is older than the sync interval
                if (DataStore.lastUpdateDelta(context) > s.getSyncInterval()) {
                    FronterService.startDownloadFeed(context);

                }


            }

        }
    }


}
