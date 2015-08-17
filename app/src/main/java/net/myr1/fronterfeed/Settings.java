package net.myr1.fronterfeed;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * Settings wrapper
 */
public class Settings {

    /**
     * Runs storage upgrades
     * @param context a context
     */
    public static void upgradeStorage(Context context){

        Settings set = new Settings(context);
        set.doStorageUpgrade();
    }


    public static final String NOTIFICATIONS = "pref_notify";
    public static final String USERNAME = "user2";
    public static final String PASSWORD = "pass2";
    public static final String FEED_URL = "fronter_feed_url";
    public static final String SYNC_WIFI = "pref_sync_wifi";
    public static final String SYNC_INTERVAL = "pref_sync_interval";
    public static final String WORKING_CONFIG = "fronter_working_config";

    private static final String STORAGE_VERSION = "storage_version";
    private static final int CURRENT_VERSION = 3;

    private Context mContext;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private static String usernameCache = "";
    private static String passwordCache = "";

    private AesCbcWithIntegrity.SecretKeys mKeys;

    public Settings(Context context)
    {
        this(context, Values.getKeys());
    }


    public Settings(Context context, AesCbcWithIntegrity.SecretKeys keys)
    {
        mKeys = keys;
        mContext = context.getApplicationContext();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
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

    public void setUsername(String username) {
        usernameCache = "";

        mEditor.putString(USERNAME, encryptString(username));
    }

    public void setPassword(String password){
        passwordCache = "";

        mEditor.putString(PASSWORD, encryptString(password));
    }

    public void setBaseFeedUrl(String url)
    {
        mEditor.putString(FEED_URL, url);
    }

    public String getUsername()
    {
        if (!TextUtils.isEmpty(usernameCache))
            return usernameCache;

        String username = mPreferences.getString(USERNAME, "");

        if (!TextUtils.isEmpty(username)){
            username = decryptString(username);
        }

        usernameCache = username;
        return username;
    }

    public String getPassword()
    {
        if (!TextUtils.isEmpty(passwordCache))
            return passwordCache;

        String password = mPreferences.getString(PASSWORD, "");

        if (!TextUtils.isEmpty(password)){
            password = decryptString(password);
        }

        passwordCache = password;
        return password;
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
        return mPreferences.getString(FEED_URL, mContext.getString(R.string.feed_url_default));
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

    public void doStorageUpgrade() {

        int version = getStorageVersion();

        if(version == CURRENT_VERSION){
            return; // Nothing to do
        }

        startEdit();


        if (version == -1){ // No previous storage
            // The version was not stored in the first version of the app so
            // we check if upgrade from 1 -> 2 is needed.
            upgradeVersion_1_2();
        }
        else if( version == 2){
            // Fix sort order of old feed items.
            fixItemOrderInVersion2();
        }

        setStorageVersion();

        commitEdit();
    }


    private int getStorageVersion(){
        return mPreferences.getInt(STORAGE_VERSION, -1);
    }
    private void setStorageVersion(){
        mEditor.putInt(STORAGE_VERSION, CURRENT_VERSION);
    }



    private void upgradeVersion_1_2(){

        String username = mPreferences.getString("fronter_username", "");
        if(!TextUtils.isEmpty(username)){
            this.setUsername(username);
            mEditor.remove("fronter_username");
        }

        String password = mPreferences.getString("fronter_password", "");

        if(!TextUtils.isEmpty(password)){
            this.setPassword(password);
            mEditor.remove("fronter_password");
        }
    }


    private void fixItemOrderInVersion2(){

        if (DataStore.hasFeedData(mContext)){
            try {
                List<FeedItem> items = DataStore.loadFeedData(mContext);
                if (!items.isEmpty()) {
                    Collections.sort(items, new FeedItem.PubDateComparator());
                    Collections.reverse(items);
                    DataStore.saveFeedData(mContext, items);
                }
            } catch (JSONException | IOException e) {

            }

        }
    }



    private String encryptString(String input) {

        AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac =
                null;

        try {
            cipherTextIvMac = AesCbcWithIntegrity.encrypt(input, mKeys);
        } catch (UnsupportedEncodingException | GeneralSecurityException e) {
            // This should never happen. If it just return the original input
            // and so it will be stored unencrypted.
            return input;
        }
        return cipherTextIvMac.toString();


    }

    private String decryptString(String input)  {

        AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac =
                new AesCbcWithIntegrity.CipherTextIvMac(input);

        try {
            return AesCbcWithIntegrity.decryptString(cipherTextIvMac, mKeys);
        } catch (UnsupportedEncodingException | GeneralSecurityException e) {
            // Just return the original input if it fails.
            return input;
        }

    }

}
