package net.myr1.fronterfeed;

import android.test.AndroidTestCase;


public class FeedItemTest extends AndroidTestCase {

    public void testEquals()
    {

        FeedItem a = new FeedItem();
        a.setType(4);
        a.setId(123);

        FeedItem b = new FeedItem();
        b.setType(4);
        b.setId(123);



        assertTrue(a.equals(b));
        assertTrue(b.equals(a));


        a = b;

        assertTrue(a.equals(b));
        assertTrue(b.equals(a));



    }


    public void testNotEquals()
    {

        FeedItem a = new FeedItem();
        a.setType(4);
        a.setId(123);

        FeedItem b = new FeedItem();
        b.setType(19);
        b.setId(123);



        assertFalse(a.equals(b));
        assertFalse(b.equals(a));


        a = new FeedItem();
        a.setType(4);
        a.setId(123);

        b = new FeedItem();
        b.setType(4);
        b.setId(124);



        assertFalse(a.equals(b));
        assertFalse(b.equals(a));



    }

}
