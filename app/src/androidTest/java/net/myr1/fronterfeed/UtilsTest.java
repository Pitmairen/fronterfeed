package net.myr1.fronterfeed;

import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class UtilsTest extends AndroidTestCase {


    public void testFixUrl()
    {

        String result = Utils.fixURL("fronter.com/test");

        assertEquals("https://fronter.com/test", result);


        result = Utils.fixURL("http://fronter.com/test");
        assertEquals("https://fronter.com/test", result);

        result = Utils.fixURL("https://fronter.com/test");
        assertEquals("https://fronter.com/test", result);


    }



    public void testConstructFonterURL() throws MalformedURLException
    {

        String urlString = "https://fronter.com/path/main.phtml";

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -30);
        Date fromDate = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-d");


        String expectedString = "https://fronter.com/path/rss/get_today_rss.php?" +
                "LANG=en&elements=4,19&fromdate=" + format.format(fromDate);


        URL url = Utils.constructFronterURL(urlString, fromDate.getTime());


        assertEquals(expectedString, url.toString());



        url = Utils.constructFronterURL("https://fronter.com/path/", fromDate.getTime());

        assertEquals(expectedString, url.toString());


        url = Utils.constructFronterURL("https://fronter.com/path", fromDate.getTime());

        assertEquals(expectedString, url.toString());


        url = Utils.constructFronterURL("https://fronter.com/path/?test=123", fromDate.getTime());

        assertEquals(expectedString, url.toString());


        url = Utils.constructFronterURL("https://fronter.com/path?test=123", fromDate.getTime());

        assertEquals(expectedString, url.toString());

    }




    public void testMalformedURL()
    {

        try{
            Utils.constructFronterURL("test fewfew", 0);
            Assert.fail("Should faile invalid url");
        }catch(MalformedURLException e){}

    }





    public void testNewItemCount()
    {

        List<FeedItem> items = new ArrayList<>();

        FeedItem item = new FeedItem();
        item.setPubDate(10);
        items.add(item);

        item = new FeedItem();
        item.setPubDate(20);
        items.add(item);

        item = new FeedItem();
        item.setPubDate(30);
        items.add(item);

        item = new FeedItem();
        item.setPubDate(30);
        items.add(item);


        int res = Utils.calculateNewItemCount(0, items);
        assertEquals(4, res);

        res = Utils.calculateNewItemCount(1, items);
        assertEquals(4, res);

        res = Utils.calculateNewItemCount(10, items);
        assertEquals(3, res);

        res = Utils.calculateNewItemCount(25, items);
        assertEquals(2, res);


        res = Utils.calculateNewItemCount(30, items);
        assertEquals(0, res);


        res = Utils.calculateNewItemCount(35, items);
        assertEquals(0, res);



    }




    public void testTrimFromDate()
    {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -30);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-d");


        long limit = calendar.getTimeInMillis();

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -25);

        long fromDate = calendar.getTimeInMillis();
        long res = Utils.trimFromDate(fromDate, 30);

        assertEquals(format.format(fromDate), format.format(res));



        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -35);

        fromDate = calendar.getTimeInMillis();
        res = Utils.trimFromDate(fromDate, 30);


        assertEquals(format.format(limit), format.format(res));

    }


    public void testTrimItemList()
    {
        Calendar calendar;


        List<FeedItem> items = new ArrayList<>();

        FeedItem item = new FeedItem();
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -10);
        item.setPubDate(calendar.getTimeInMillis());
        items.add(item);

        item = new FeedItem();
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -20);
        item.setPubDate(calendar.getTimeInMillis());
        items.add(item);

        item = new FeedItem();
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -29);
        item.setPubDate(calendar.getTimeInMillis());
        items.add(item);

        item = new FeedItem();
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -31);
        item.setPubDate(calendar.getTimeInMillis());
        items.add(item);


        item = new FeedItem();
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -40);
        item.setPubDate(calendar.getTimeInMillis());
        items.add(item);


        List<FeedItem> allItems = new ArrayList<>(items);


        Utils.trimFeedItems(items, 30);
        assertEquals(3, items.size());
        assertEquals(allItems.get(0), items.get(0));
        assertEquals(allItems.get(1), items.get(1));
        assertEquals(allItems.get(2), items.get(2));


    }




    public void testAddFeedItems()
    {
        List<FeedItem> items = new ArrayList<>();

        FeedItem item = new FeedItem();
        item.setId(123);
        item.setType(4);
        items.add(item);

        item = new FeedItem();
        item.setId(124);
        item.setType(4);
        items.add(item);

        item = new FeedItem();
        item.setId(125);
        item.setType(19);
        items.add(item);


        List<FeedItem> newItems = new ArrayList<>();

        item = new FeedItem();
        item.setId(125);
        item.setType(4);
        newItems.add(item);

        item = new FeedItem();
        item.setId(124);
        item.setType(4);
        newItems.add(item);

        item = new FeedItem();
        item.setId(124);
        item.setType(19);
        newItems.add(item);

        item = new FeedItem();
        item.setId(124);
        item.setType(19);
        newItems.add(item);


        List<FeedItem> expects = new ArrayList<>(items);
        expects.add(newItems.get(0));
        expects.add(newItems.get(2));


        List<FeedItem> res = Utils.addToFeedItems(items, newItems);


        assertEquals(expects.size(), res.size());
        assertEquals(expects, res);



    }

}
