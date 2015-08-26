package net.myr1.fronterfeed;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Global helper object used to enable and disable auto sync/notification,
 * and check that the config is valid.
 */
public class Features implements SharedPreferences.OnSharedPreferenceChangeListener {


    private static Features instance = null;

    /**
     * Returns the Features instance
     * @return the instance
     */
    public static Features getInstance()
    {

        return instance;

    }

    /**
     * Creates the Fatures instance.
     * Should be called from the application onCreate method.
     * @param context a context
     */
    public static void createInstance(Context context)
    {
        instance = new Features(context);
    }



    private Settings mSettings;
    private Context mContext;
    private Notifications mNotifications;
    private FronterSync mSync;
    private FailHandler mFailHandler;


    private Features(Context context)
    {
        mContext = context.getApplicationContext();
        mSettings = new Settings(mContext);

        mNotifications = new Notifications(mContext);
        mFailHandler = new FailHandler(mContext);
        mFailHandler.register();

        mSync = new FronterSync(mContext);
        mSettings.getPreferencesObject().registerOnSharedPreferenceChangeListener(this);
    }


    /**
     * Returns true if auto sync can be done. This check for connectivity based on
     * the users preferences (Use wifi or not).
     * @return true if sync can be done
     */
    public boolean canAutoSync()
    {
        return mSettings.syncOnWIFIOnly() ? Connectivity.isOnlineWIFI(mContext) :
                Connectivity.isOnline(mContext);
    }



    public void removeActiveNotification(){

        mNotifications.removeActiveNotification();

    }


    /**
     * Enables the notifications if the current settings are working.
     *
     * This can be called multiple times and will only enable notifications
     * if the current settings allows for it.
     *
     * It will also enable the auto sync.
     */
    public void enableNotifications()
    {
        if(!shouldEnableNotifications()){
            return;
        }

        doEnableNotifications();


    }

    /**
     * Disables the notifications.
     *
     * It will also disable auto sync.
     */
    public void disableNotifications()
    {

        if(!shouldEnableNotifications()){
            return;
        }

        doDisableNotifications();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


        if(key.equals(Settings.NOTIFICATIONS) || key.equals(Settings.WORKING_CONFIG)){
            if(shouldEnableNotifications()){
                doEnableNotifications();
                doEnableBootReceviver();
            }else{
                doDisableNotifications();
                doDisableBootReceviver();
                // Disable connectivity receiver if it has been enabled.
                Connectivity.disableConnectivityReceviver(mContext);
            }
        }
        else if(key.equals(Settings.SYNC_INTERVAL)){
            if(shouldEnableNotifications())
                doEnableSync();
            else
                doDisableSync();
        }
    }



    private boolean shouldEnableNotifications()
    {
        return mSettings.notificationsEnabled() &&
                mSettings.hasRequiredSettings()  &&
                mSettings.hasWorkingConfig();
    }


    private void doEnableNotifications()
    {
        IntentFilter statusFilter = new IntentFilter(FronterService.BROADCAST_STATUS);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mNotifications, statusFilter);

        doEnableSync();
    }

    private void doDisableNotifications()
    {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mNotifications);

        doDisableSync();

    }


    private void doEnableSync()
    {
        mSync.disableSync();
        mSync.scheduleUpdates(mSettings.getSyncInterval());
    }

    private void doDisableSync()
    {
        mSync.disableSync();
    }



    private void doEnableBootReceviver()
    {
        ComponentName receiver = new ComponentName(mContext, BootReceiver.class);
        PackageManager pm = mContext.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }

    private void doDisableBootReceviver()
    {
        ComponentName receiver = new ComponentName(mContext, BootReceiver.class);
        PackageManager pm = mContext.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

    }

}
