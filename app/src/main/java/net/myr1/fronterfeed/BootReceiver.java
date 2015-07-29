package net.myr1.fronterfeed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast receiver used to enable the sync/notifications after reboot.
 */
public class BootReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            Settings s = new Settings(context);
            if(!s.notificationsEnabled() || s.hasWorkingConfig())
                return;

            // Sync if network is available else enable the connectivity receiver.
            if(Features.getInstance().canAutoSync()){

                Features.getInstance().enableNotifications();

                // Update the data if the data is older than the sync interval
                if (DataStore.lastUpdateDelta(context) > s.getSyncInterval()) {
                    FronterService.startDownloadFeed(context);
                }

            }else{

                Connectivity.enableConnectivityReceiver(context);

            }

        }


    }

}
