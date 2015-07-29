package net.myr1.fronterfeed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Monitors the feed download status and updates the config
 * to reflect if the current config is working.
 *
 * A valid config means that the username, password and feed url is valid and working.
 * This can be checked with the hasWorkingConfig method on the Settings object.
 *
 * This setting is used to enable or disable auto sync based on if the current config is
 * valid.
 *
 */
public class FailHandler {

    private Context mContext;
    private Settings mSettings;

    private Receiver mReceiver;


    public FailHandler(Context context)
    {
        mContext = context.getApplicationContext();
        mSettings = new Settings(mContext);

        mReceiver = new Receiver();

    }

    /**
     * Enables the monitoring.
     */
    public void register()
    {
        IntentFilter statusFilter = new IntentFilter(FronterService.BROADCAST_STATUS);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, statusFilter);
    }


    private class Receiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent == null)
                return;

            final String status = intent.getStringExtra(FronterService.STATUS);


            // If auth or url fail the config is not working.
            if(status.equals(FronterService.STATUS_AUTH_FAIL) || status.equals(FronterService.STATUS_URL_FAIL)){
                mSettings.startEdit();
                mSettings.setHasWorkingConfig(false);
                mSettings.commitEdit();
                mSettings.startEdit();

            }
            else if(status.equals(FronterService.STATUS_SUCCESS)){

                if(!mSettings.hasWorkingConfig()){
                    mSettings.startEdit();
                    mSettings.setHasWorkingConfig(true);
                    mSettings.commitEdit();
                    mSettings.startEdit();
                }

            }




        }

    }


}
