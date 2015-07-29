package net.myr1.fronterfeed;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;


public class FronterService extends IntentService {


    public static final String ACTION_DOWNLOAD_FEED = "net.myr1.fronterfeed.action.DOWNLOAD_FEED";

    public static final String BROADCAST_STATUS = "net.myr1.fronterfeed.action.BROADCAST_STATUS";


    public static final String STATUS = "net.myr1.fronterfeed.status.STATUS";
    public static final String STATUS_MESSAGE = "net.myr1.fronterfeed.status.STATUS_MESSAGE";
    public static final String STATUS_NEW_ITEMS = "net.myr1.fronterfeed.status.STATUS_NEW_ITEMS";


    public static final String STATUS_AUTH_FAIL = "net.myr1.fronterfeed.status.AUTH_FAIL";
    public static final String STATUS_URL_FAIL = "net.myr1.fronterfeed.status.URL_FAIL";

    public static final String STATUS_NO_NETWORK_FAIL = "net.myr1.fronterfeed.status.NO_NETWORK_FAIL";

    public static final String STATUS_FAILED = "net.myr1.fronterfeed.status.FAILED";
    public static final String STATUS_SUCCESS = "net.myr1.fronterfeed.status.SUCCESS";


    /**
     * Starts a new download of the feed.
     *
     *  The feed is by default download over wifi only, unless the user has changed
     *  the settings to allow non-wifi downloads.
     *
     * @param context a context
     */
    public static void startDownloadFeed(Context context) {
        Intent intent = new Intent(context, FronterService.class);
        intent.setAction(FronterService.ACTION_DOWNLOAD_FEED);
        context.startService(intent);
    }

    /**
     * Starts a new download of the feed in manual mode.
     *
     * This will download the feed if internet is available.
     *
     * @param context a context
     */
    public static void startDownloadFeedManual(Context context) {
        Intent intent = new Intent(context, FronterService.class);
        intent.setAction(FronterService.ACTION_DOWNLOAD_FEED);
        intent.putExtra("isAutoSync", false);

        context.startService(intent);
    }



    private Settings mSettings;


    public FronterService() {
        super("FronterService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (FronterService.ACTION_DOWNLOAD_FEED.equals(action)) {
                boolean isAutoSync = intent.getBooleanExtra("isAutoSync", true);
                mSettings = new Settings(this);
                handleActionDownloadFeed(isAutoSync);
            }
        }
    }



    private void handleActionDownloadFeed(boolean isAutoSync) {

        if (!mSettings.hasUsernameAndPassword()) {
            broadCastStatus(FronterService.STATUS_AUTH_FAIL, "");
            return;
        } else if (!mSettings.hasBaseFeedUrl()) {
            broadCastStatus(FronterService.STATUS_URL_FAIL, "");
            return;
        }

        runRequest(mSettings.getBaseFeedUrl(), mSettings.getUsername(), mSettings.getPassword(), isAutoSync);


    }


    private void runRequest(String url, String username, String password, boolean isAutoSync)
    {

        // If its an auto sync download based on the user settings.
        // If its an manual sync download if we are online.
        if(isAutoSync){

            if(!Features.getInstance().canAutoSync()){
                broadCastStatus(FronterService.STATUS_NO_NETWORK_FAIL, getString(R.string.no_network_available_desc));

                //TODO: Maybe this can be moved out of this class
                Features.getInstance().disableNotifications();
                Connectivity.enableConnectivityReceiver(this);
                return;
            }
        }
        else if(!Connectivity.isOnline(this)){
            broadCastStatus(FronterService.STATUS_NO_NETWORK_FAIL, getString(R.string.no_network_available_desc));
            return;
        }


        Fronter fronter = new Fronter(username, password);

        try {

            long lastUpdate = DataStore.lastUpdated(this).getTime();

            // no older than 30 days
            long fromDate = Utils.trimFromDate(lastUpdate, 30);

            String data = fronter.get(Utils.constructFronterURL(url, fromDate));

            FeedParser parser = new FeedParser();

            List<FeedItem> items = parser.parse(data);

            DataStore.addFeedItems(this, items);

            int newItems = Utils.calculateNewItemCount(lastUpdate, items);

            broadCastStatusSucess(newItems);


        }
       catch (Fronter.AuthenticationFailure e) {

            broadCastStatus(FronterService.STATUS_AUTH_FAIL, getString(R.string.error_authentication_failed));

        } catch(MalformedURLException e) {

            broadCastStatus(FronterService.STATUS_URL_FAIL, getString(R.string.error_invalid_feed_url));

        } catch(FileNotFoundException e){

            broadCastStatus(FronterService.STATUS_URL_FAIL, getString(R.string.error_feed_url_not_found));

        } catch(JSONException | FeedParser.FeedException | IOException e){
            broadCastStatus(FronterService.STATUS_FAILED, e.toString());
        }


    }



    private void broadCastStatus(String status, String msg)
    {
        Intent intent = new Intent(FronterService.BROADCAST_STATUS)
                        .putExtra(FronterService.STATUS, status)
                        .putExtra(FronterService.STATUS_MESSAGE, msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    private void broadCastStatusSucess(int new_items)
    {
        Intent intent = new Intent(FronterService.BROADCAST_STATUS)
                .putExtra(FronterService.STATUS, FronterService.STATUS_SUCCESS)
                .putExtra(FronterService.STATUS_MESSAGE, "")
                .putExtra(FronterService.STATUS_NEW_ITEMS, new_items);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }



}
