package net.myr1.fronterfeed;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Helper methods for connectivity.
 */
public class Connectivity {


    /**
     * Returns true if the device is online.
     *
     * @param context a Context object
     * @return true if the device is online
     */
    public static boolean isOnline(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected();

    }


    /**
     * Returns true if the device is online using WIFI.
     *
     * @param context a Context object
     * @return true if the device is online using WIFI
     */
    public static boolean isOnlineWIFI(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected() &&
                netInfo.getType() == ConnectivityManager.TYPE_WIFI;

    }


    /**
     * Enables the broadcast receiver that will restart sync/notifications once a
     * network connection is available.
     *
     * @param context a Context object
     */
    public static void enableConnectivityReceiver(Context context)
    {
        ComponentName receiver = new ComponentName(context, ConnectivityReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }


    /**
     * Disables the connectivity broadcast receiver.
     *
     * @param context a Context object
     */
    public static void disableConnectivityReceviver(Context context)
    {
        ComponentName receiver = new ComponentName(context, ConnectivityReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

    }
}
