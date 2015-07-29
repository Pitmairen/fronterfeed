package net.myr1.fronterfeed;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * Settings wrapper
 */
public class Settings {


    public static final String NOTIFICATIONS = "pref_notify";
    public static final String USERNAME = "fronter_username";
    public static final String PASSWORD = "fronter_password";
    public static final String FEED_URL = "fronter_feed_url";
    public static final String SYNC_WIFI = "pref_sync_wifi";
    public static final String SYNC_INTERVAL = "pref_sync_interval";
    public static final String WORKING_CONFIG = "fronter_working_config";


    private SharedPreferences mPreferences;

    private SharedPreferences.Editor mEditor;


    public Settings(Context context)
    {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public SharedPreferences getPreferencesObject()
    {
        return mPreferences;
    }

    public boolean hasBaseFeedUrl()
    {
        return !TextUtils.isEmpty(getBaseFeedUrl());
    }

    public boolean hasUsernameAndPassword()
    {
        return !TextUtils.isEmpty(getUsername()) &&
                !TextUtils.isEmpty(getPassword());
    }

    public boolean hasRequiredSettings()
    {
        return !TextUtils.isEmpty(getUsername()) &&
                !TextUtils.isEmpty(getPassword()) &&
                !TextUtils.isEmpty(getBaseFeedUrl());
    }

    public void startEdit()
    {
        mEditor = mPreferences.edit();
    }

    public void stopEdit()
    {
        mEditor = null;
    }

    public void commitEdit()
    {
        mEditor.commit();
    }

    public void setUsername(String username)
    {
        mEditor.putString(USERNAME, username);
    }

    public void setPassword(String password)
    {
        mEditor.putString(PASSWORD, password);
    }

    public void setBaseFeedUrl(String url)
    {
        mEditor.putString(FEED_URL, url);
    }

    public String getUsername()
    {
        return mPreferences.getString(USERNAME, "");
    }

    public String getPassword()
    {
        return mPreferences.getString(PASSWORD, "");
    }

    public boolean hasWorkingConfig()
    {
        return mPreferences.getBoolean(WORKING_CONFIG, false);
    }

    public void setHasWorkingConfig(boolean value)
    {
        mEditor.putBoolean(WORKING_CONFIG, value);
    }


    public String getBaseFeedUrl()
    {
        return mPreferences.getString(FEED_URL, "");
    }


    public boolean syncOnWIFIOnly()
    {
        return mPreferences.getBoolean(SYNC_WIFI, true);
    }


    public boolean notificationsEnabled()
    {
        return mPreferences.getBoolean(NOTIFICATIONS, true);
    }


    public long getSyncInterval()
    {
        int value = Integer.parseInt(mPreferences.getString(SYNC_INTERVAL, "2"));

        switch(value){
            case 0:
                return AlarmManager.INTERVAL_FIFTEEN_MINUTES;
            case 1:
                return AlarmManager.INTERVAL_HALF_HOUR;
            case 2:
                return AlarmManager.INTERVAL_HOUR;
            case 3:
                return AlarmManager.INTERVAL_HALF_DAY;
            case 4:
                return AlarmManager.INTERVAL_DAY;
            default:
                return AlarmManager.INTERVAL_HOUR;

        }
    }



}
