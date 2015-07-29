package net.myr1.fronterfeed;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Manages storage of the feed data.
 */
public class DataStore {

    private static final String FILENAME = "feed-data.json";


    /**
     * Returns a DataStore object for the context.
     * @param context a context object
     * @return A DataStore object for the context
     */
    private static DataStore getStore(Context context)
    {
        return new DataStore(context.getApplicationContext(), FILENAME);
    }


    /**
     * Saves the feed items.
     *
     * The data is stored as JSON.
     *
     * @param context a context
     * @param items The item list to store
     * @throws IOException
     * @throws JSONException
     */
    public static void saveFeedData(Context context, List<FeedItem> items) throws IOException, JSONException
    {
        JSONArray json = JSONSerializer.itemListToJSON(items);
        getStore(context).save(json.toString().getBytes());
    }


    /**
     * Load the stored feed data.
     *
     * @param context a context
     * @return a list with the stored feed items
     * @throws IOException
     * @throws JSONException
     */
    public static List<FeedItem> loadFeedData(Context context) throws IOException, JSONException
    {
        return JSONSerializer.jsonToItemList(new JSONArray(getStore(context).loadData()));
    }


    /**
     * Adds the new items to the existing ones.
     * It will remove items that are older than 30 days.
     *
     * @param context a context
     * @param newItems list of new items
     * @throws IOException
     * @throws JSONException
     */
    public static void addFeedItems(Context context, List<FeedItem> newItems) throws IOException, JSONException
    {
        if(!hasFeedData(context)){
            saveFeedData(context, newItems);
            return;
        }

        List<FeedItem> currentItems = loadFeedData(context);

        currentItems = Utils.addToFeedItems(currentItems, newItems);

        // Removed items older than 30 days
        Utils.trimFeedItems(currentItems, 30);

        saveFeedData(context, currentItems);

    }



    /**
     * Checks if the data store file exists.
     *
     * @param context a context
     * @return true if the data store exits even if it is empty.
     */
    public static boolean hasFeedData(Context context)
    {
        return getStore(context).hasData();
    }


    /**
     * Returns the Date of the last update.
     * @param context a conects
     * @return the date of the last update of the data store.
     */
    public static Date lastUpdated(Context context)
    {
        return getStore(context).getLastUpdated();
    }

    /**
     * Returns the difference between the current time and the last update.
     * @param context a context
     * @return difference between current time and last update. (millis)
     */
    public static long lastUpdateDelta(Context context)
    {
        return System.currentTimeMillis() - lastUpdated(context).getTime();
    }



    /**
     * Deletes the current data file.
     *
     * @param context a context
     */
    public static void deleteFeedData(Context context)
    {
        getStore(context).delete();
    }




    private Context mContext;
    private String mFilename;


    private DataStore(Context context, String filename)
    {
        mContext = context.getApplicationContext();
        mFilename = filename;
    }


    public String loadData() throws IOException {

        FileInputStream in = mContext.openFileInput(mFilename);

        byte[] bytes = new byte[(int)getFile().length()];

        try {

            in.read(bytes);

            return new String(bytes);
        }
        finally{
            in.close();
        }
    }



    public boolean hasData()
    {
        return getFile().exists();
    }


    public void delete()
    {
        mContext.deleteFile(mFilename);
    }


    public Date getLastUpdated()
    {
        if(hasData()){
            return new Date(getFile().lastModified());
        }
        return new Date(0);
    }


    public void save(byte[] data) throws IOException
    {

        FileOutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);

        try {
            out.write(data);
        }
        finally {
            out.close();
        }
    }

    private File getFile()
    {
        return mContext.getFileStreamPath(this.mFilename);

    }

}
