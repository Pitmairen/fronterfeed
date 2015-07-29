package net.myr1.fronterfeed;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Parser for the fronter rss feed.
 */
public class FeedParser {

    /**
     * Exception for general feed parsing exceptions
     */
    public class FeedException extends Exception
    {
        public FeedException(String message)
        {
            super(message);
        }
    }


    // no namespace
    private static final String ns = null;


    /**
     * Parses the input string and returns a list of feed items
     * @param in input string
     * @return a list of FeedItem's
     * @throws FeedException
     * @throws IOException
     */
    public List<FeedItem> parse(String in) throws FeedException, IOException
    {
        InputStream stream = new ByteArrayInputStream(in.getBytes());
        try{
            return this.parse(stream);
        }finally {
            stream.close();
        }
    }

    /**
     * Parses the input stream and returns a list of feed items
     * @param in input stream
     * @return a list of FeedItem's
     * @throws FeedException
     * @throws IOException
     */
    public  List<FeedItem> parse(InputStream in) throws IOException, FeedException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, "UTF-8");
            parser.nextTag();
            return readFeed(parser);
        }
        catch(ParseException | XmlPullParserException e){
            throw new FeedException(e.toString());
        }
    }



    // Parses the feed
    private List<FeedItem> readFeed(XmlPullParser parser) throws IOException, XmlPullParserException, ParseException, FeedException
    {
        List<FeedItem> items = new ArrayList<>();


        parser.require(XmlPullParser.START_TAG, ns, "rss");
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, ns, "channel");

        while (!parsingIsDone(parser)) {

                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();

                if (name.equals("item")) {
                    items.add(readItem(parser));
                }
                else {
                    skip(parser);
                }

        }

        return items;

    }



    // Parses a single feed item.
    private FeedItem readItem(XmlPullParser parser) throws XmlPullParserException, IOException, FeedException, ParseException {

        parser.require(XmlPullParser.START_TAG, ns, "item");


        // set with the required item values
        HashSet<String> expectedTags = new HashSet<>(Arrays.asList(
            "fronterXinfo:item_id", "fronterXinfo:roomname", "fronterXinfo:item_type",
                "title", "author", "pubDate", "description"
        ));

        FeedItem item = new FeedItem();

        while (!parsingIsDone(parser)) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if(expectedTags.contains(name)){

                switch(name){

                    case "pubDate":
                        item.setPubDate(parseDate(readTextTag(parser, name)));
                        break;
                    case "description":
                        item.setDescription(parseDescription(parser, name));
                        break;
                    case "title":
                        item.setTitle(readTextTag(parser, name));
                        break;
                    case "author":
                        item.setAuthor(readTextTag(parser, name));
                        break;
                    case "fronterXinfo:item_id":
                        item.setId(parseItemId(readTextTag(parser, name)));
                        break;
                    case "fronterXinfo:item_type":
                        item.setType(Integer.parseInt(readTextTag(parser, name)));
                        break;
                    case "fronterXinfo:roomname":
                        item.setRoomName(readTextTag(parser, name));
                        break;

                }

                expectedTags.remove(name);

            }else{
                skip(parser);
            }

        }

        if(expectedTags.size() != 0){
            throw new FeedException("Missing item values: " + expectedTags.toString());
        }else if(!(item.getType() == FeedItem.TYPE_MESSAGE || item.getType() == FeedItem.TYPE_NEWS)){
            throw new FeedException("Unexpected item type: " + item.getType());
        }


        return item;
    }


    // The id string should be on the form "4_12345"
    private int parseItemId(String content) throws FeedException
    {

        String[] parts = content.split("_");

        if(parts.length != 2){
            throw new FeedException("Unexpected item id");
        }

        try {
            return Integer.parseInt(parts[1]);
        }catch(NumberFormatException e){
            throw new FeedException(e.toString());
        }

    }


    // The description tag contains a CDATA block
    private String parseDescription(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException, FeedException
    {
        parser.require(XmlPullParser.START_TAG, ns, tagName);

        skipText(parser);

        int type = parser.getEventType();

        String data;

        if(type == XmlPullParser.CDSECT){
            data =  parser.getText().trim();
        }else{
            // If there was no cdata we fail
            throw new FeedException("Invalid description tag");
        }

        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, tagName);

        return data;
    }


    // Returns true when ant the end of the document or end of a tag.
    private boolean parsingIsDone(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        int next = parser.next();
        return next == XmlPullParser.END_TAG || next == XmlPullParser.END_DOCUMENT;
    }


    private long parseDate(String date) throws ParseException
    {
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy k:m:s ZZZ", Locale.US);

        return format.parse(date).getTime();

    }

    private String readTextTag(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tagName);
        String text = readText(parser).trim();
        parser.require(XmlPullParser.END_TAG, ns, tagName);
        return text;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skipText(XmlPullParser parser) throws XmlPullParserException, IOException {

        while(parser.nextToken() == XmlPullParser.TEXT)
            ;
    }

    // Skips the current tag and all its child tags.
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
                case XmlPullParser.END_DOCUMENT:
                    depth = 0;
                    break;
            }
        }
    }
}
