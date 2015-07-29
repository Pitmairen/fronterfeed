package net.myr1.fronterfeed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter for the feed list
 */
public class FeedListAdapter extends BaseAdapter {

    private Context mContext;
    private List<FeedItem> mItems;

    public FeedListAdapter(Context context, List<FeedItem> items) {
        mContext = context;
        mItems = items;

    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.feedlist_layout, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.title = (TextView) convertView.findViewById(R.id.feeditem_title);
            viewHolder.author = (TextView) convertView.findViewById(R.id.feeditem_author);
            viewHolder.date = (TextView) convertView.findViewById(R.id.feeditem_date);
            viewHolder.room = (TextView) convertView.findViewById(R.id.feeditem_room);

            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FeedItem item = mItems.get(position);

        viewHolder.title.setText(item.getTitle());
        viewHolder.author.setText(item.getAuthor());
        viewHolder.room.setText(item.getRoomName());
        viewHolder.date.setText(item.getPubDateString());

        return convertView;

    }



    private static class ViewHolder {
        public TextView title;
        public TextView author;
        public TextView date;
        public TextView room;

    }
}
