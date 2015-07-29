package net.myr1.fronterfeed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts the feed item list to and from JSON.
 */
public class JSONSerializer {


    /**
     * Converts the item list to a JSON array
     * @param items list of FeedItems
     * @return A JSONArray of JSONObjects
     * @throws JSONException
     */
    public static JSONArray itemListToJSON(List<FeedItem> items) throws JSONException
    {
        ToJSON s = new ToJSON(items);

        return s.toJSON();

    }

    /**
     * Converts a JSONArray of feed items to a List of FeedItems
     * @param json a list of JSONObjects
     * @return a List of FeedItems
     * @throws JSONException
     */
    public static List<FeedItem> jsonToItemList(JSONArray json) throws JSONException
    {
        FromJSON s = new FromJSON(json);

        return s.toList();

    }


    private static class ToJSON{

        private List<FeedItem> mItems;

        public ToJSON(List<FeedItem> items)
        {
            mItems = items;
        }

        public JSONArray toJSON() throws JSONException
        {
            JSONArray json = new JSONArray();

            for(FeedItem item : mItems){
                json.put(itemToJSON(item));
            }

            return json;
        }

        public JSONObject itemToJSON(FeedItem item) throws JSONException
        {
            JSONObject json = new JSONObject();

            json.put("title", item.getTitle());
            json.put("author", item.getAuthor());
            json.put("pubDate", item.getPubDate());
            json.put("description", item.getDescription());
            json.put("itemId", item.getId());
            json.put("roomName", item.getRoomName());
            json.put("type", item.getType());

            return json;

        }

    }

    private static class FromJSON {

        private JSONArray mItems;

        public FromJSON(JSONArray items)
        {
            mItems = items;
        }

        public List<FeedItem> toList() throws JSONException
        {
            List<FeedItem> items = new ArrayList<>();

            int len = mItems.length();
            for(int i=0; i < len; i++){
                items.add(JSONToItem(mItems.getJSONObject(i)));
            }

            return items;
        }

        public FeedItem JSONToItem(JSONObject json) throws JSONException
        {
            FeedItem item = new FeedItem();

            item.setTitle(json.getString("title"));
            item.setAuthor(json.getString("author"));
            item.setPubDate(json.getLong("pubDate"));
            item.setDescription(json.getString("description"));
            item.setId(json.getInt("itemId"));
            item.setRoomName(json.getString("roomName"));
            item.setType(json.getInt("type"));


            return item;

        }

    }



}
