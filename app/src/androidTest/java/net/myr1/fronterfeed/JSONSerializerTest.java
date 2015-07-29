package net.myr1.fronterfeed;

import android.test.AndroidTestCase;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class JSONSerializerTest extends AndroidTestCase {


    private List<FeedItem> mItems;


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mItems = new ArrayList<>();

        FeedItem item = new FeedItem();

        item.setId(1);
        item.setAuthor("Author1");
        item.setTitle("Title1");
        item.setRoomName("Room1");
        item.setPubDate(123456789);
        item.setType(FeedItem.TYPE_MESSAGE);
        item.setDescription("<h2>Description1</h2>");

        mItems.add(item);

        item = new FeedItem();

        item.setId(2);
        item.setAuthor("Author2");
        item.setTitle("Title2");
        item.setRoomName("Room2");
        item.setPubDate(123456789);
        item.setType(FeedItem.TYPE_MESSAGE);
        item.setDescription("<h2>Description2</h2>");

        mItems.add(item);


    }


    public void testToJSON() throws JSONException
    {

        JSONArray json = JSONSerializer.itemListToJSON(mItems);

        assertEquals(2, json.length());

        JSONObject item = json.getJSONObject(0);

        assertEquals(1, item.getInt("itemId"));
        assertEquals("Title1", item.getString("title"));
        assertEquals("Author1", item.getString("author"));
        assertEquals("<h2>Description1</h2>", item.getString("description"));
        assertEquals("Room1", item.getString("roomName"));
        assertEquals(FeedItem.TYPE_MESSAGE, item.getInt("type"));
        assertEquals(123456789, item.getLong("pubDate"));


        item = json.getJSONObject(1);

        assertEquals(2, item.getInt("itemId"));
        assertEquals("Title2", item.getString("title"));
        assertEquals("Author2", item.getString("author"));
        assertEquals("<h2>Description2</h2>", item.getString("description"));
        assertEquals("Room2", item.getString("roomName"));
        assertEquals(FeedItem.TYPE_MESSAGE, item.getInt("type"));
        assertEquals(123456789, item.getLong("pubDate"));

    }



    public void testFromJSON() throws JSONException
    {

        JSONArray json = JSONSerializer.itemListToJSON(mItems);


        List<FeedItem> items = JSONSerializer.jsonToItemList(json);


        assertEquals(mItems.size(), items.size());


        for(int i=0; i < 2; i++) {

            FeedItem item1 = mItems.get(i);
            FeedItem item2 = items.get(i);

            assertEquals(item1.getId(), item2.getId());
            assertEquals(item1.getTitle(), item2.getTitle());
            assertEquals(item1.getAuthor(), item2.getAuthor());
            assertEquals(item1.getPubDate(), item2.getPubDate());
            assertEquals(item1.getDescription(), item2.getDescription());
            assertEquals(item1.getType(), item2.getType());
            assertEquals(item1.getRoomName(), item2.getRoomName());
        }


    }


    public void testEmptyToJSON() throws JSONException
    {

        JSONArray json = JSONSerializer.itemListToJSON(new ArrayList<FeedItem>());

        assertEquals(0, json.length());

    }



    public void testEmptyFromJSON() throws JSONException
    {

        List<FeedItem> items = JSONSerializer.jsonToItemList(new JSONArray());

        assertEquals(0, items.size());

    }



    public void testInvalidJSONMissingValues() throws JSONException
    {

        JSONArray json = new JSONArray();
        JSONObject item = createJSONItem();
        item.remove("title");
        json.put(item);

        try{
            JSONSerializer.jsonToItemList(json);
            Assert.fail("Should fail, missing title");
        }catch(JSONException e){}


        json = new JSONArray();
        item = createJSONItem();
        item.remove("itemId");
        json.put(item);

        try{
            JSONSerializer.jsonToItemList(json);
            Assert.fail("Should fail, missing id");
        }catch(JSONException e){}



        json = new JSONArray();
        item = createJSONItem();
        item.remove("author");
        json.put(item);

        try{
            JSONSerializer.jsonToItemList(json);
            Assert.fail("Should fail, missing author");
        }catch(JSONException e){}



        json = new JSONArray();
        item = createJSONItem();
        item.remove("description");
        json.put(item);

        try{
            JSONSerializer.jsonToItemList(json);
            Assert.fail("Should fail, missing description");
        }catch(JSONException e){}


        json = new JSONArray();
        item = createJSONItem();
        item.remove("type");
        json.put(item);

        try{
            JSONSerializer.jsonToItemList(json);
            Assert.fail("Should fail, missing type id");
        }catch(JSONException e){}


        json = new JSONArray();
        item = createJSONItem();
        item.remove("roomName");
        json.put(item);

        try{
            JSONSerializer.jsonToItemList(json);
            Assert.fail("Should fail, missing roomName");
        }catch(JSONException e){}


        json = new JSONArray();
        item = createJSONItem();
        item.remove("pubDate");
        json.put(item);

        try{
            JSONSerializer.jsonToItemList(json);
            Assert.fail("Should fail, missing pubDate");
        }catch(JSONException e){}



    }


    public void testInvalidJSONPubDate() throws JSONException
    {

        JSONArray json = new JSONArray();
        JSONObject item = createJSONItem();

        item.put("pubDate", "2015-01-01 10:11:12"); // String value
        json.put(item);

        try{
            JSONSerializer.jsonToItemList(json);
            Assert.fail("Should fail, string date");
        }catch(JSONException e){}


    }

    public void testInvalidJSONType() throws JSONException
    {

        JSONArray json = new JSONArray();
        JSONObject item = createJSONItem();

        item.put("type", "Four"); // String value
        json.put(item);

        try{
            JSONSerializer.jsonToItemList(json);
            Assert.fail("Should fail, string type id");
        }catch(JSONException e){}


    }



    private JSONObject createJSONItem() throws JSONException
    {
        return createJSONItem("");
    }

    private JSONObject createJSONItem(String sufix) throws JSONException
    {
        JSONObject item = new JSONObject();
        item.put("itemId", 1);
        item.put("title", "Title" + sufix);
        item.put("author", "Author" + sufix);
        item.put("description", "<h2>Description" + sufix + "</h2>");
        item.put("type", FeedItem.TYPE_MESSAGE);
        item.put("roomName", "Room" + sufix);
        item.put("pubDate", 123456789);

        return item;


    }

}
