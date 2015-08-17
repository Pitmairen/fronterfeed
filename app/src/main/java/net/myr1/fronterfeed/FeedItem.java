package net.myr1.fronterfeed;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;

/**
 * Represents a feed item
 *
 */
public class FeedItem {

    // Id's matching the item id's on fronter
    public static final int TYPE_MESSAGE = 4;
    public static final int TYPE_NEWS = 19;



    private int mId;
    private String mRoomName;
    private int mType;
    private String mTitle;
    private String mDescription;
    private String mAuthor;
    private long mPubDate;

    public FeedItem()
    {

    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FeedItem))
            return false;
        if (obj == this)
            return true;

        FeedItem item = (FeedItem) obj;

        return item.getType() == getType() && item.getId() == getId();

    }


    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + getId();
        hashCode = 31 * hashCode + getType();
        return hashCode;
    }





    public int getId()
    {
        return mId;
    }

    public void setId(int id)
    {
        mId = id;
    }

    public String getRoomName() {
        return mRoomName;
    }

    public void setRoomName(String roomName) {
        mRoomName = roomName;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    /**
     * Returns the pub date in millis
     * @return the pub date in millis
     */
    public long getPubDate() {
        return mPubDate;
    }

    public void setPubDate(long pubDate) {
        mPubDate = pubDate;
    }


    public String getPubDateString() {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getPubDate());
        DateFormat df = SimpleDateFormat.getDateTimeInstance();
        return df.format(cal.getTime());
    }





    public static class PubDateComparator implements Comparator<FeedItem> {
        @Override
        public int compare(FeedItem a, FeedItem b) {
            int cmp = a.getPubDate() > b.getPubDate() ? +1 :
                    a.getPubDate() < b.getPubDate() ? -1 : 0;
            return cmp;
        }
    }

}
