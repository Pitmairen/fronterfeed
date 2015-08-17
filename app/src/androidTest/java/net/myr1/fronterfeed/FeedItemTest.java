package net.myr1.fronterfeed;

import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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



    public void testSortingPubDate()
    {

        FeedItem a = new FeedItem();
        a.setType(4);
        a.setId(1);
        a.setPubDate(1);

        FeedItem b = new FeedItem();
        b.setType(4);
        b.setId(2);
        b.setPubDate(50);

        FeedItem c = new FeedItem();
        c.setType(4);
        c.setId(3);
        c.setPubDate(20);

        FeedItem d = new FeedItem();
        d.setType(4);
        d.setId(4);
        d.setPubDate(25);


        List<FeedItem> list = new ArrayList<>();

        list.add(a);
        list.add(b);
        list.add(c);
        list.add(d);


        List<FeedItem> expected = new ArrayList<>();
        expected.add(a);
        expected.add(c);
        expected.add(d);
        expected.add(b);


        assertFalse(expected.equals(list));


        Collections.sort(list, new FeedItem.PubDateComparator());


        assertEquals(expected, list);





    }

}
