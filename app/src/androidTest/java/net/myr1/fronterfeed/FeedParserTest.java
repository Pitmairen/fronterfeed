package net.myr1.fronterfeed;

import android.test.AndroidTestCase;

//import junit.framework.Assert;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class FeedParserTest extends AndroidTestCase {


    public void testParseString() throws IOException, FeedParser.FeedException {

        final String input = "<rss xmlns:fronterXinfo=\"http://fronter.com/fronter/rss/fronterXinfo\" version=\"2.0\">\n" +
                "<channel>\n" +
                "<title>FeedTitle</title>\n" +
                "<link>https://fronter.com/</link>\n" +
                "<description>Fronter RSS feed</description>\n" +
                "<language>en</language>\n" +
                "<item>\n" +
                "<fronterXinfo:item_id>4_123</fronterXinfo:item_id>\n" +
                "<fronterXinfo:line_no>0</fronterXinfo:line_no>\n" +
                "<fronterXinfo:roomname>Room1</fronterXinfo:roomname>\n" +
                "<fronterXinfo:author>Author1</fronterXinfo:author>\n" +
                "<fronterXinfo:item_type>4</fronterXinfo:item_type>\n" +
                "<title>\n" +
                "Title1" +
                "</title>\n" +
                "<link/>\n" +
                "<pubDate>Tue, 16 Jun 2015 22:14:56 +0200</pubDate>\n" +
                "<author>Author1</author>\n" +
                "<guid isPermaLink=\"false\">6e17b3d4bbdb6fc138733371a98191bc</guid>\n" +
                "<description>\n" +
                "<![CDATA[\n" +
                "<h2>Description1</h2>" +
                "]]>\n" +
                "</description>\n" +
                "</item>\n" +
                "<item>\n" +
                "<fronterXinfo:item_id>4_124</fronterXinfo:item_id>\n" +
                "<fronterXinfo:line_no>0</fronterXinfo:line_no>\n" +
                "<fronterXinfo:roomname>Room2</fronterXinfo:roomname>\n" +
                "<fronterXinfo:author>Author2</fronterXinfo:author>\n" +
                "<fronterXinfo:item_type>4</fronterXinfo:item_type>\n" +
                "<title>\n" +
                "Title2" +
                "</title>\n" +
                "<link/>\n" +
                "<pubDate>Tue, 3 Jun 2014 21:14:56 -0600</pubDate>\n" +
                "<author>Author2</author>\n" +
                "<guid isPermaLink=\"false\">6e17b3d4bbdb6fc138733371a98191ba</guid>\n" +
                "<description>\n" +
                "<![CDATA[\n" +
                "<h2>Description2</h2>" +
                "]]>\n" +
                "</description>\n" +
                "</item>\n" +
                "</channel>" +
                "</rss>";


        FeedParser parser = new FeedParser();


        List<FeedItem> res = parser.parse(input);

        assertEquals(2, res.size());

        assertEquals(123, res.get(0).getId());
        assertEquals(124, res.get(1).getId());
        assertEquals("Title1", res.get(0).getTitle());
        assertEquals("Title2", res.get(1).getTitle());
        assertEquals("Author1", res.get(0).getAuthor());
        assertEquals("Author2", res.get(1).getAuthor());
        assertEquals("Room1", res.get(0).getRoomName());
        assertEquals("Room2", res.get(1).getRoomName());
        assertEquals(4, res.get(0).getType());
        assertEquals(4, res.get(1).getType());

        assertEquals("<h2>Description1</h2>", res.get(0).getDescription());
        assertEquals("<h2>Description2</h2>", res.get(1).getDescription());


        assertEquals(1434485696000L, res.get(0).getPubDate());
        assertEquals(1401851696000L, res.get(1).getPubDate());

    }


    public void testMissingRss() throws IOException {

        final String input = "<rssa xmlns:fronterXinfo=\"http://fronter.com/fronter/rss/fronterXinfo\" version=\"2.0\">\n" +
                "<channel>" +
                "<title>FeedTitle</title>\n" +
                "<link>https://fronter.com/</link>\n" +
                "</rss>";

        FeedParser parser = new FeedParser();

        try {
            parser.parse(input);

            Assert.fail("Should throw FeedException");
        } catch (FeedParser.FeedException e) {
        }


    }

    public void testMissingChannel() throws IOException {

        final String input = "<rss xmlns:fronterXinfo=\"http://fronter.com/fronter/rss/fronterXinfo\" version=\"2.0\">\n" +
                "<title>FeedTitle</title>\n" +
                "<link>https://fronter.com/</link>\n" +
                "</rss>";

        FeedParser parser = new FeedParser();

        try {
            parser.parse(input);

            Assert.fail("Should throw FeedException");
        } catch (FeedParser.FeedException e) {
        }


    }


    public void testMissingItemData() throws IOException, FeedParser.FeedException {

        String input = "<rss xmlns:fronterXinfo=\"http://fronter.com/fronter/rss/fronterXinfo\" version=\"2.0\">\n" +
                "<channel>\n" +
                "<title>FeedTitle</title>\n" +
                "<link>https://fronter.com/</link>\n" +
                "<description>Fronter RSS feed</description>\n" +
                "<language>en</language>\n" +
                "<item>\n" +
                "<fronterXinfo:item_id>4_123</fronterXinfo:item_id>\n" +
                "<fronterXinfo:line_no>0</fronterXinfo:line_no>\n" +
                "<fronterXinfo:roomname>Room1</fronterXinfo:roomname>\n" +
                "<fronterXinfo:author>Author1</fronterXinfo:author>\n" +
                "<fronterXinfo:item_type>4</fronterXinfo:item_type>\n" +
                "<title>\n" +
                "Title1" +
                "</title>\n" +
                "<link/>\n" +
                "<pubDate>Tue, 16 Jun 2015 22:14:56 +0200</pubDate>\n" +
                "<author>Author1</author>\n" +
                "<guid isPermaLink=\"false\">6e17b3d4bbdb6fc138733371a98191bc</guid>\n" +
                "<description>\n" +
                "<![CDATA[\n" +
                "<h2>Description1</h2>" +
                "]]>\n" +
                "</description>\n" +
                "</item>";


        FeedParser parser = new FeedParser();

        parser.parse(input); // Should work



        input = "<rss xmlns:fronterXinfo=\"http://fronter.com/fronter/rss/fronterXinfo\" version=\"2.0\">\n" +
                "<channel>\n" +
                "<title>FeedTitle</title>\n" +
                "<link>https://fronter.com/</link>\n" +
                "<description>Fronter RSS feed</description>\n" +
                "<language>en</language>\n" +
                "<item>\n" +
                //"<fronterXinfo:item_id>4_123</fronterXinfo:item_id>\n" +
                "<fronterXinfo:line_no>0</fronterXinfo:line_no>\n" +
                "<fronterXinfo:roomname>Room1</fronterXinfo:roomname>\n" +
                "<fronterXinfo:author>Author1</fronterXinfo:author>\n" +
                "<fronterXinfo:item_type>4</fronterXinfo:item_type>\n" +
                "<title>\n" +
                "Title1" +
                "</title>\n" +
                "<link/>\n" +
                "<pubDate>Tue, 16 Jun 2015 22:14:56 +0200</pubDate>\n" +
                "<author>Author1</author>\n" +
                "<guid isPermaLink=\"false\">6e17b3d4bbdb6fc138733371a98191bc</guid>\n" +
                "<description>\n" +
                "<![CDATA[\n" +
                "<h2>Description1</h2>" +
                "]]>\n" +
                "</description>\n" +
                "</item>";


        try {
            parser.parse(input);
            Assert.fail("Should Fail missing item id");
        } catch (FeedParser.FeedException e) {
        }



        input = "<rss xmlns:fronterXinfo=\"http://fronter.com/fronter/rss/fronterXinfo\" version=\"2.0\">\n" +
                "<channel>\n" +
                "<title>FeedTitle</title>\n" +
                "<link>https://fronter.com/</link>\n" +
                "<description>Fronter RSS feed</description>\n" +
                "<language>en</language>\n" +
                "<item>\n" +
                "<fronterXinfo:item_id>4_123</fronterXinfo:item_id>\n" +
                "<fronterXinfo:line_no>0</fronterXinfo:line_no>\n" +
                //"<fronterXinfo:roomname>Room1</fronterXinfo:roomname>\n" +
                "<fronterXinfo:author>Author1</fronterXinfo:author>\n" +
                "<fronterXinfo:item_type>4</fronterXinfo:item_type>\n" +
                "<title>\n" +
                "Title1" +
                "</title>\n" +
                "<link/>\n" +
                "<pubDate>Tue, 16 Jun 2015 22:14:56 +0200</pubDate>\n" +
                "<author>Author1</author>\n" +
                "<guid isPermaLink=\"false\">6e17b3d4bbdb6fc138733371a98191bc</guid>\n" +
                "<description>\n" +
                "<![CDATA[\n" +
                "<h2>Description1</h2>" +
                "]]>\n" +
                "</description>\n" +
                "</item>";


        try {
            parser.parse(input);
            Assert.fail("Should Fail missing room name");
        } catch (FeedParser.FeedException e) {
        }


        input = "<rss xmlns:fronterXinfo=\"http://fronter.com/fronter/rss/fronterXinfo\" version=\"2.0\">\n" +
                "<channel>\n" +
                "<title>FeedTitle</title>\n" +
                "<link>https://fronter.com/</link>\n" +
                "<description>Fronter RSS feed</description>\n" +
                "<language>en</language>\n" +
                "<item>\n" +
                "<fronterXinfo:item_id>4_123</fronterXinfo:item_id>\n" +
                "<fronterXinfo:line_no>0</fronterXinfo:line_no>\n" +
                "<fronterXinfo:roomname>Room1</fronterXinfo:roomname>\n" +
                "<fronterXinfo:author>Author1</fronterXinfo:author>\n" +
                //"<fronterXinfo:item_type>4</fronterXinfo:item_type>\n" +
                "<title>\n" +
                "Title1" +
                "</title>\n" +
                "<link/>\n" +
                "<pubDate>Tue, 16 Jun 2015 22:14:56 +0200</pubDate>\n" +
                "<author>Author1</author>\n" +
                "<guid isPermaLink=\"false\">6e17b3d4bbdb6fc138733371a98191bc</guid>\n" +
                "<description>\n" +
                "<![CDATA[\n" +
                "<h2>Description1</h2>" +
                "]]>\n" +
                "</description>\n" +
                "</item>";


        try {
            parser.parse(input);
            Assert.fail("Should Fail missing item type");
        } catch (FeedParser.FeedException e) {
        }


        input = "<rss xmlns:fronterXinfo=\"http://fronter.com/fronter/rss/fronterXinfo\" version=\"2.0\">\n" +
                "<channel>\n" +
                "<title>FeedTitle</title>\n" +
                "<link>https://fronter.com/</link>\n" +
                "<description>Fronter RSS feed</description>\n" +
                "<language>en</language>\n" +
                "<item>\n" +
                "<fronterXinfo:item_id>4_123</fronterXinfo:item_id>\n" +
                "<fronterXinfo:line_no>0</fronterXinfo:line_no>\n" +
                "<fronterXinfo:roomname>Room1</fronterXinfo:roomname>\n" +
                "<fronterXinfo:author>Author1</fronterXinfo:author>\n" +
                "<fronterXinfo:item_type>4</fronterXinfo:item_type>\n" +
                //"<title>\n" +
                //"Title1" +
                //"</title>\n" +
                "<link/>\n" +
                "<pubDate>Tue, 16 Jun 2015 22:14:56 +0200</pubDate>\n" +
                "<author>Author1</author>\n" +
                "<guid isPermaLink=\"false\">6e17b3d4bbdb6fc138733371a98191bc</guid>\n" +
                "<description>\n" +
                "<![CDATA[\n" +
                "<h2>Description1</h2>" +
                "]]>\n" +
                "</description>\n" +
                "</item>";


        try {
            parser.parse(input);
            Assert.fail("Should Fail missing title");
        } catch (FeedParser.FeedException e) {
        }


        input = "<rss xmlns:fronterXinfo=\"http://fronter.com/fronter/rss/fronterXinfo\" version=\"2.0\">\n" +
                "<channel>\n" +
                "<title>FeedTitle</title>\n" +
                "<link>https://fronter.com/</link>\n" +
                "<description>Fronter RSS feed</description>\n" +
                "<language>en</language>\n" +
                "<item>\n" +
                "<fronterXinfo:item_id>4_123</fronterXinfo:item_id>\n" +
                "<fronterXinfo:line_no>0</fronterXinfo:line_no>\n" +
                "<fronterXinfo:roomname>Room1</fronterXinfo:roomname>\n" +
                "<fronterXinfo:author>Author1</fronterXinfo:author>\n" +
                "<fronterXinfo:item_type>4</fronterXinfo:item_type>\n" +
                "<title>\n" +
                "Title1" +
                "</title>\n" +
                "<link/>\n" +
                //"<pubDate>Tue, 16 Jun 2015 22:14:56 +0200</pubDate>\n" +
                "<author>Author1</author>\n" +
                "<guid isPermaLink=\"false\">6e17b3d4bbdb6fc138733371a98191bc</guid>\n" +
                "<description>\n" +
                "<![CDATA[\n" +
                "<h2>Description1</h2>" +
                "]]>\n" +
                "</description>\n" +
                "</item>";


        try {
            parser.parse(input);
            Assert.fail("Should Fail missing pub date");
        } catch (FeedParser.FeedException e) {
        }


        input = "<rss xmlns:fronterXinfo=\"http://fronter.com/fronter/rss/fronterXinfo\" version=\"2.0\">\n" +
                "<channel>\n" +
                "<title>FeedTitle</title>\n" +
                "<link>https://fronter.com/</link>\n" +
                "<description>Fronter RSS feed</description>\n" +
                "<language>en</language>\n" +
                "<item>\n" +
                "<fronterXinfo:item_id>4_123</fronterXinfo:item_id>\n" +
                "<fronterXinfo:line_no>0</fronterXinfo:line_no>\n" +
                "<fronterXinfo:roomname>Room1</fronterXinfo:roomname>\n" +
                "<fronterXinfo:author>Author1</fronterXinfo:author>\n" +
                "<fronterXinfo:item_type>4</fronterXinfo:item_type>\n" +
                "<title>\n" +
                "Title1" +
                "</title>\n" +
                "<link/>\n" +
                "<pubDate>Tue, 16 Jun 2015 22:14:56 +0200</pubDate>\n" +
                //"<author>Author1</author>\n" +
                "<guid isPermaLink=\"false\">6e17b3d4bbdb6fc138733371a98191bc</guid>\n" +
                "<description>\n" +
                "<![CDATA[\n" +
                "<h2>Description1</h2>" +
                "]]>\n" +
                "</description>\n" +
                "</item>";


        try {
            parser.parse(input);
            Assert.fail("Should Fail missing author");
        } catch (FeedParser.FeedException e) {
        }




        input = "<rss xmlns:fronterXinfo=\"http://fronter.com/fronter/rss/fronterXinfo\" version=\"2.0\">\n" +
                "<channel>\n" +
                "<title>FeedTitle</title>\n" +
                "<link>https://fronter.com/</link>\n" +
                "<description>Fronter RSS feed</description>\n" +
                "<language>en</language>\n" +
                "<item>\n" +
                "<fronterXinfo:item_id>4_123</fronterXinfo:item_id>\n" +
                "<fronterXinfo:line_no>0</fronterXinfo:line_no>\n" +
                "<fronterXinfo:roomname>Room1</fronterXinfo:roomname>\n" +
                "<fronterXinfo:author>Author1</fronterXinfo:author>\n" +
                "<fronterXinfo:item_type>4</fronterXinfo:item_type>\n" +
                "<title>\n" +
                "Title1" +
                "</title>\n" +
                "<link/>\n" +
                "<pubDate>Tue, 16 Jun 2015 22:14:56 +0200</pubDate>\n" +
                "<author>Author1</author>\n" +
                "<guid isPermaLink=\"false\">6e17b3d4bbdb6fc138733371a98191bc</guid>\n" +
                //"<description>\n" +
                //"<![CDATA[\n" +
                //"<h2>Description1</h2>" +
                //"]]>\n" +
                //"</description>\n" +
                "</item>";


        try {
            parser.parse(input);
            Assert.fail("Should Fail missing description");
        } catch (FeedParser.FeedException e) {
        }


    }



    public void testInvalidId() throws IOException {

        String input = "<rss xmlns:fronterXinfo=\"http://fronter.com/fronter/rss/fronterXinfo\" version=\"2.0\">\n" +
                "<channel>\n" +
                "<title>FeedTitle</title>\n" +
                "<link>https://fronter.com/</link>\n" +
                "<description>Fronter RSS feed</description>\n" +
                "<language>en</language>\n" +
                "<item>\n" +
                "<fronterXinfo:item_id>123</fronterXinfo:item_id>\n" +
                "<fronterXinfo:line_no>0</fronterXinfo:line_no>\n" +
                "<fronterXinfo:roomname>Room1</fronterXinfo:roomname>\n" +
                "<fronterXinfo:author>Author1</fronterXinfo:author>\n" +
                "<fronterXinfo:item_type>4</fronterXinfo:item_type>\n" +
                "<title>\n" +
                "Title1" +
                "</title>\n" +
                "<link/>\n" +
                "<pubDate>16 Jun 2015 22:14:56 +0200</pubDate>\n" +
                "<author>Author1</author>\n" +
                "<guid isPermaLink=\"false\">6e17b3d4bbdb6fc138733371a98191bc</guid>\n" +
                "<description>\n" +
                "<h2>Description1</h2>" +
                "</description>\n" +
                "</item>";


        FeedParser parser = new FeedParser();


        try {
            parser.parse(input);
            Assert.fail("Should Fail Invalid Id");
        } catch (FeedParser.FeedException e) {
        }




        input = "<rss xmlns:fronterXinfo=\"http://fronter.com/fronter/rss/fronterXinfo\" version=\"2.0\">\n" +
                "<channel>\n" +
                "<title>FeedTitle</title>\n" +
                "<link>https://fronter.com/</link>\n" +
                "<description>Fronter RSS feed</description>\n" +
                "<language>en</language>\n" +
                "<item>\n" +
                "<fronterXinfo:item_id>12_abc</fronterXinfo:item_id>\n" +
                "<fronterXinfo:line_no>0</fronterXinfo:line_no>\n" +
                "<fronterXinfo:roomname>Room1</fronterXinfo:roomname>\n" +
                "<fronterXinfo:author>Author1</fronterXinfo:author>\n" +
                "<fronterXinfo:item_type>4</fronterXinfo:item_type>\n" +
                "<title>\n" +
                "Title1" +
                "</title>\n" +
                "<link/>\n" +
                "<pubDate>16 Jun 2015 22:14:56 +0200</pubDate>\n" +
                "<author>Author1</author>\n" +
                "<guid isPermaLink=\"false\">6e17b3d4bbdb6fc138733371a98191bc</guid>\n" +
                "<description>\n" +
                "<h2>Description1</h2>" +
                "</description>\n" +
                "</item>";


        try {
            parser.parse(input);
            Assert.fail("Should Fail Invalid Id (string)");
        } catch (FeedParser.FeedException e) {
        }


    }



    public void testInvalidDescription() throws IOException {

        String input = "<rss xmlns:fronterXinfo=\"http://fronter.com/fronter/rss/fronterXinfo\" version=\"2.0\">\n" +
                "<channel>\n" +
                "<title>FeedTitle</title>\n" +
                "<link>https://fronter.com/</link>\n" +
                "<description>Fronter RSS feed</description>\n" +
                "<language>en</language>\n" +
                "<item>\n" +
                "<fronterXinfo:item_id>4_123</fronterXinfo:item_id>\n" +
                "<fronterXinfo:line_no>0</fronterXinfo:line_no>\n" +
                "<fronterXinfo:roomname>Room1</fronterXinfo:roomname>\n" +
                "<fronterXinfo:author>Author1</fronterXinfo:author>\n" +
                "<fronterXinfo:item_type>4</fronterXinfo:item_type>\n" +
                "<title>\n" +
                "Title1" +
                "</title>\n" +
                "<link/>\n" +
                "<pubDate>16 Jun 2015 22:14:56 +0200</pubDate>\n" +
                "<author>Author1</author>\n" +
                "<guid isPermaLink=\"false\">6e17b3d4bbdb6fc138733371a98191bc</guid>\n" +
                "<description>\n" +
                "<h2>Description1</h2>" +
                "</description>\n" +
                "</item>";


        FeedParser parser = new FeedParser();


        try {
            parser.parse(input);
            Assert.fail("Should Fail Invalid Description data");
        } catch (FeedParser.FeedException e) {
        }
    }




    public void testInvalidDate() throws IOException {

        String input = "<rss xmlns:fronterXinfo=\"http://fronter.com/fronter/rss/fronterXinfo\" version=\"2.0\">\n" +
                "<channel>\n" +
                "<title>FeedTitle</title>\n" +
                "<link>https://fronter.com/</link>\n" +
                "<description>Fronter RSS feed</description>\n" +
                "<language>en</language>\n" +
                "<item>\n" +
                "<fronterXinfo:item_id>4_123</fronterXinfo:item_id>\n" +
                "<fronterXinfo:line_no>0</fronterXinfo:line_no>\n" +
                "<fronterXinfo:roomname>Room1</fronterXinfo:roomname>\n" +
                "<fronterXinfo:author>Author1</fronterXinfo:author>\n" +
                "<fronterXinfo:item_type>4</fronterXinfo:item_type>\n" +
                "<title>\n" +
                "Title1" +
                "</title>\n" +
                "<link/>\n" +
                "<pubDate>16 Jun 2015 22:14:56 +0200</pubDate>\n" +
                "<author>Author1</author>\n" +
                "<guid isPermaLink=\"false\">6e17b3d4bbdb6fc138733371a98191bc</guid>\n" +
                "<description>\n" +
                "<![CDATA[\n" +
                "<h2>Description1</h2>" +
                "]]>\n" +
                "</description>\n" +
                "</item>";


        FeedParser parser = new FeedParser();


        try {
            parser.parse(input);
            Assert.fail("Should Fail unexpected date");
        } catch (FeedParser.FeedException e) {
        }
    }

    public void testInvalidItemType() throws IOException {

        String input = "<rss xmlns:fronterXinfo=\"http://fronter.com/fronter/rss/fronterXinfo\" version=\"2.0\">\n" +
                "<channel>\n" +
                "<title>FeedTitle</title>\n" +
                "<link>https://fronter.com/</link>\n" +
                "<description>Fronter RSS feed</description>\n" +
                "<language>en</language>\n" +
                "<item>\n" +
                "<fronterXinfo:item_id>4_123</fronterXinfo:item_id>\n" +
                "<fronterXinfo:line_no>0</fronterXinfo:line_no>\n" +
                "<fronterXinfo:roomname>Room1</fronterXinfo:roomname>\n" +
                "<fronterXinfo:author>Author1</fronterXinfo:author>\n" +
                "<fronterXinfo:item_type>2</fronterXinfo:item_type>\n" +
                "<title>\n" +
                "Title1" +
                "</title>\n" +
                "<link/>\n" +
                "<pubDate>16 Jun 2015 22:14:56 +0200</pubDate>\n" +
                "<author>Author1</author>\n" +
                "<guid isPermaLink=\"false\">6e17b3d4bbdb6fc138733371a98191bc</guid>\n" +
                "<description>\n" +
                "<![CDATA[\n" +
                "<h2>Description1</h2>" +
                "]]>\n" +
                "</description>\n" +
                "</item>";


        FeedParser parser = new FeedParser();


        try {
            parser.parse(input);
            Assert.fail("Should Fail invalid item type");
        } catch (FeedParser.FeedException e) {
        }

    }





    public void testWithNewsItem() throws IOException, FeedParser.FeedException {

        final String input = "<rss xmlns:fronterXinfo=\"http://fronter.com/fronter/rss/fronterXinfo\" version=\"2.0\">\n" +
                "<channel>\n" +
                "<title>FeedTitle</title>\n" +
                "<link>https://fronter.com/</link>\n" +
                "<description>Fronter RSS feed</description>\n" +
                "<language>en</language>\n" +
                "<item>\n" +
                "<fronterXinfo:item_id>4_123</fronterXinfo:item_id>\n" +
                "<fronterXinfo:line_no>0</fronterXinfo:line_no>\n" +
                "<fronterXinfo:roomname>Room1</fronterXinfo:roomname>\n" +
                "<fronterXinfo:author>Author1</fronterXinfo:author>\n" +
                "<fronterXinfo:item_type>4</fronterXinfo:item_type>\n" +
                "<title>\n" +
                "Title1" +
                "</title>\n" +
                "<link/>\n" +
                "<pubDate>Tue, 16 Jun 2015 22:14:56 +0200</pubDate>\n" +
                "<author>Author1</author>\n" +
                "<guid isPermaLink=\"false\">6e17b3d4bbdb6fc138733371a98191bc</guid>\n" +
                "<description>\n" +
                "<![CDATA[\n" +
                "<h2>Description1</h2>" +
                "]]>\n" +
                "</description>\n" +
                "</item>\n" +
                "<item>\n" +
                "<fronterXinfo:item_id>19_124</fronterXinfo:item_id>\n" +
                "<fronterXinfo:line_no>0</fronterXinfo:line_no>\n" +
                "<fronterXinfo:roomname>Room2</fronterXinfo:roomname>\n" +
                "<fronterXinfo:author>Author2</fronterXinfo:author>\n" +
                "<fronterXinfo:item_type>19</fronterXinfo:item_type>\n" +
                "<title>\n" +
                "Title2" +
                "</title>\n" +
                "<link/>\n" +
                "<pubDate>Tue, 3 Jun 2014 21:14:56 -0600</pubDate>\n" +
                "<author>Author2</author>\n" +
                "<guid isPermaLink=\"false\">6e17b3d4bbdb6fc138733371a98191ba</guid>\n" +
                "<description>\n" +
                "<![CDATA[\n" +
                "<h2>Description2</h2>" +
                "]]>\n" +
                "</description>\n" +
                "</item>\n" +
                "</channel>" +
                "</rss>";


        FeedParser parser = new FeedParser();


        List<FeedItem> res = parser.parse(input);

        assertEquals(2, res.size());


        assertEquals("Title1", res.get(0).getTitle());
        assertEquals("Title2", res.get(1).getTitle());
        assertEquals("Author1", res.get(0).getAuthor());
        assertEquals("Author2", res.get(1).getAuthor());
        assertEquals("Room1", res.get(0).getRoomName());
        assertEquals("Room2", res.get(1).getRoomName());
        assertEquals(4, res.get(0).getType());
        assertEquals(19, res.get(1).getType());

        assertEquals("<h2>Description1</h2>", res.get(0).getDescription());
        assertEquals("<h2>Description2</h2>", res.get(1).getDescription());

        assertEquals(1434485696000L, res.get(0).getPubDate());
        assertEquals(1401851696000L, res.get(1).getPubDate());

    }





    public void testEmptyRoom() throws IOException, FeedParser.FeedException {

        final String input = "<rss xmlns:fronterXinfo=\"http://fronter.com/fronter/rss/fronterXinfo\" version=\"2.0\">\n" +
                "<channel>\n" +
                "<title>FeedTitle</title>\n" +
                "<link>https://fronter.com/</link>\n" +
                "<description>Fronter RSS feed</description>\n" +
                "<language>en</language>\n" +
                "<item>\n" +
                "<fronterXinfo:item_id>4_123</fronterXinfo:item_id>\n" +
                "<fronterXinfo:line_no>0</fronterXinfo:line_no>\n" +
                "<fronterXinfo:roomname>Room1</fronterXinfo:roomname>\n" +
                "<fronterXinfo:author>Author1</fronterXinfo:author>\n" +
                "<fronterXinfo:item_type>4</fronterXinfo:item_type>\n" +
                "<title>\n" +
                "Title1" +
                "</title>\n" +
                "<link/>\n" +
                "<pubDate>Tue, 16 Jun 2015 22:14:56 +0200</pubDate>\n" +
                "<author>Author1</author>\n" +
                "<guid isPermaLink=\"false\">6e17b3d4bbdb6fc138733371a98191bc</guid>\n" +
                "<description>\n" +
                "<![CDATA[\n" +
                "<h2>Description1</h2>" +
                "]]>\n" +
                "</description>\n" +
                "</item>\n" +
                "<item>\n" +
                "<fronterXinfo:item_id>19_124</fronterXinfo:item_id>\n" +
                "<fronterXinfo:line_no>0</fronterXinfo:line_no>\n" +
                "<fronterXinfo:roomname/>\n" +
                "<fronterXinfo:author>Author2</fronterXinfo:author>\n" +
                "<fronterXinfo:item_type>19</fronterXinfo:item_type>\n" +
                "<title>\n" +
                "Title2" +
                "</title>\n" +
                "<link/>\n" +
                "<pubDate>Tue, 3 Jun 2014 21:14:56 -0600</pubDate>\n" +
                "<author>Author2</author>\n" +
                "<guid isPermaLink=\"false\">6e17b3d4bbdb6fc138733371a98191ba</guid>\n" +
                "<description>\n" +
                "<![CDATA[\n" +
                "<h2>Description2</h2>" +
                "]]>\n" +
                "</description>\n" +
                "</item>\n" +
                "</channel>" +
                "</rss>";


        FeedParser parser = new FeedParser();


        List<FeedItem> res = parser.parse(input);

        assertEquals(2, res.size());

        assertEquals("Room1", res.get(0).getRoomName());
        assertEquals("", res.get(1).getRoomName());


    }




}
