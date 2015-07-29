package net.myr1.fronterfeed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * The main feed list activity.
 *
 */
public class FeedActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // Used to check the status of the download service.
    private FeedReceiver mReceiver;

    private View mProgressView;
    private ListView mListView;
    private View mEmptyView;

    private Toolbar mToolBar;

    private FeedListAdapter mFeedListAdapter;

    private List<FeedItem> mFeedData;
    private Settings mSettings;
    private long mLastFeedReload = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);


        mToolBar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(mToolBar);

        mProgressView = findViewById(R.id.feed_progress);

        mFeedData = new ArrayList<>();
        mListView = (ListView) findViewById(R.id.list_view);
        mFeedListAdapter = new FeedListAdapter(this, mFeedData);
        mEmptyView = findViewById(R.id.empty_list);
        mListView.setEmptyView(mEmptyView);
        mListView.setAdapter(mFeedListAdapter);
        mListView.setOnItemClickListener(this);

        mReceiver = new FeedReceiver();
        mSettings = new Settings(this);

    }

    @Override
    protected void onResume() {

        super.onResume();

        showProgress(true);

        if(!mSettings.hasRequiredSettings()){
            showLoginForm();
        }
        else if(!DataStore.hasFeedData(this)){
            refreshFeedManual();
        }
        else if(mSettings.notificationsEnabled() &&
                ((DataStore.lastUpdateDelta(this) > mSettings.getSyncInterval()) ||
                        (DataStore.lastUpdateDelta(this) > 1000L*60*60))){

            refreshFeedManual();
        }
        else if(mLastFeedReload < DataStore.lastUpdated(this).getTime()){
            reloadData();
        }else{
            showProgress(false);
        }


        // Disable notifications when this activity is active.
        Features.getInstance().disableNotifications();


        // Listen for fronter serivce status messages.
        IntentFilter statusFilter = new IntentFilter(FronterService.BROADCAST_STATUS);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, statusFilter);

    }


    @Override
    protected void onPause() {
        super.onPause();


        // Re enable notifications
        Features.getInstance().enableNotifications();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);

    }


    /**
     * Click handler for the list view.
     */
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {

        showFeedItem(mFeedData.get(position));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_logout) {

            logOut();
            showLoginForm();
            return true;

        }else if(id == R.id.action_refresh){

            refreshFeedManual();
            return true;

        }else if(id == R.id.action_settings){

            showSettingsForm();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }



    private void showFeedItem(FeedItem feedItem)
    {
        Intent intent = new Intent();
        intent.setClass(this, MessageActivity.class);
        intent.putExtra("description", feedItem.getDescription());
        startActivity(intent);
    }


    private void showSettingsForm()
    {
        Intent intent = new Intent();
        intent.setClass(this, SettingsActivity.class);

        startActivity(intent);
    }


    private void refreshFeedManual()
    {
        FronterService.startDownloadFeedManual(this);
        showProgress(true);
    }

    /**
     *  Reset the username and password and delete the data file.
     */
    private void logOut()
    {
        mSettings.startEdit();
        mSettings.setUsername("");
        mSettings.setPassword("");
        mSettings.commitEdit();
        mSettings.stopEdit();

        DataStore.deleteFeedData(this);


    }


    private void showLoginForm(String errorMessage)
    {
        Intent intent = new Intent(this, LoginActivity.class);

        intent.putExtra(LoginActivity.ERROR_MESSAGE_EXTRA, errorMessage);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }


    private void showLoginForm()
    {
        showLoginForm("");
    }


    private void showProgress(final boolean show) {


        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mListView.setVisibility(show ? View.GONE : View.VISIBLE);
        if(show)
            mEmptyView.setVisibility(View.GONE);
        else if(mFeedData.isEmpty())
            mEmptyView.setVisibility(View.VISIBLE);


    }


    private void reloadData()
    {

        if(DataStore.hasFeedData(this)) {
            try {

                mFeedData = DataStore.loadFeedData(this);

                mFeedListAdapter = new FeedListAdapter(this, mFeedData);
                mListView.setAdapter(this.mFeedListAdapter);
                mLastFeedReload = System.currentTimeMillis();

            } catch (JSONException | IOException e) {
                Utils.showMessageDialog(this, getString(R.string.failed_to_load_data), e.toString());
            }
        }

        showProgress(false);
    }



    // Broadcast Receiver for receiving status updates from the Fronter service
    private class FeedReceiver extends BroadcastReceiver
    {
        private FeedReceiver() {}


        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent == null)
                return;

            final String status = intent.getStringExtra(FronterService.STATUS);
            final String msg = intent.getStringExtra(FronterService.STATUS_MESSAGE);


            switch(status){

                case FronterService.STATUS_AUTH_FAIL:
                    showLoginForm(msg);
                    break;

                case FronterService.STATUS_URL_FAIL:
                    showLoginForm(msg);
                    break;

                case FronterService.STATUS_NO_NETWORK_FAIL:
                    Utils.showMessageDialog(FeedActivity.this, getString(R.string.no_network_available_title), msg);
                    reloadData();
                    break;

                case FronterService.STATUS_FAILED:
                    Utils.showMessageDialog(FeedActivity.this, getString(R.string.sync_failed_title), msg);
                    reloadData();
                    break;

                case FronterService.STATUS_SUCCESS:
                    reloadData();
                    break;

            }

        }
    }


}
