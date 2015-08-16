package net.myr1.fronterfeed;


import android.content.Context;
import android.support.v7.app.AlertDialog;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;


/**
 * Utils
 */
public class Utils {


    /**
     * Adds https:// to the url if it is missing.
     * @param urlString an url
     * @return a url
     */
    public static String fixURL(String urlString)
    {


        if(urlString.startsWith("https://")){
            return urlString;

        }
        else if(urlString.startsWith("http://")){
            return urlString.replaceFirst("http://", "https://");
        }


        return "https://" + urlString;

    }


    /**
     * Creates the feed url from the base url provided by the user.
     *
     * The base url should be something like:
     * https://fronter.com/myschool/
     *
     * The returned value should be:
     * https://fronter.com/myschool/rss/get_today_rss.php?...
     *
     * @param urlString the base url provided by the user
     * @param fromDate the date of the oldes items to get (in millis).
     * @return the feed url to download
     * @throws MalformedURLException
     */
    public static URL constructFronterURL(String urlString, long fromDate) throws MalformedURLException
    {

        URL base = new URL(urlString);


        // Try to add trailing / to the path if it is missing.
        if(!base.getPath().endsWith("/")){

            if(!base.getPath().endsWith("html")){

                base = new URL(base, base.getPath() + "/");
            }

        }


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(fromDate);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-d");

        String fromDateString = format.format(calendar.getTime());

        return new URL(base, "rss/get_today_rss.php?LANG=en&elements=4,19&fromdate=" + fromDateString);


    }


    /**
     * Shows a dialog to the user.
     *
     * @param context a context
     * @param title The title of the dialog
     * @param message The message to show.
     */
    public static void showMessageDialog(Context context, String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message).setTitle(title);
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();

        dialog.show();
    }


    /**
     * Calculates the number of items newer than lastUpdate in the list of items.
     * @param lastUpdate the time threshold. Items newer than this are counted.
     * @param items list of feed items
     * @return the number of items newer than lastUpdate.
     */
    public static int calculateNewItemCount(long lastUpdate, List<FeedItem> items)
    {
        int newCount = 0;

        for (FeedItem item : items) {

            if (item.getPubDate() > lastUpdate)
                newCount++;

        }
        return newCount;

    }


    /**
     * Makes sure fromDate is not older than daysLimit days.
     *
     * @param fromDate a date in millis
     * @param daysLimit the number of days back that is the limit for the fromDate.
     * @return fromDate unchanged if its not older than daysLimit else current time - daysLimit
     */
    public static long trimFromDate(long fromDate, int daysLimit)
    {
        
        Calendar fromLimit = Calendar.getInstance();
        fromLimit.add(Calendar.DATE, -daysLimit);


        Calendar from = Calendar.getInstance();
        from.setTimeInMillis(fromDate);


        if(from.before(fromLimit)){
            fromDate = fromLimit.getTimeInMillis();
        }

        return fromDate;
    }


    /**
     *  Removed items that are older than daysLimit from the items lists.
     * @param items a list of items
     * @param daysLimit the max age in number of days.
     */
    public static void trimFeedItems(List<FeedItem> items, int daysLimit)
    {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -daysLimit);

        long limit = calendar.getTimeInMillis();

        Iterator<FeedItem> it = items.iterator();

        while(it.hasNext()){

            FeedItem item = it.next();

            if(item.getPubDate() < limit){
                it.remove();
            }
        }

    }


    /**
     * Adds new items to the current items.
     *
     * New items are only added if they are not already part of the current items.
     * @param currentItems a list of current items
     * @param newItems a list of new items
     * @return a new list of items.
     */
    public static List<FeedItem> addToFeedItems(List<FeedItem> currentItems, List<FeedItem> newItems)
    {
        int len = newItems.size();
        for(int i=0; i < len; i++){
            FeedItem item = newItems.get(len - i - 1);
            if(!currentItems.contains(item))
                currentItems.add(0, item);
        }
        return currentItems;

    }



}
