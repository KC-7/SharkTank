package kkmp.sharktank;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import static kkmp.sharktank.R.layout.list_item;

/**
 * Created by kchugh on 1/30/2017 at 8:48 PM
 */

class ListViewAdapter extends ArrayAdapter<HashMap<String, String>> {

    public ListViewAdapter(Context context, ArrayList<HashMap<String, String>> data) {
        super(context, R.layout.list_item, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater infl = LayoutInflater.from(getContext());
        View listing_item_view = infl.inflate(list_item, parent, false);

        TextView title = (TextView)listing_item_view.findViewById(R.id.listing_title);
        TextView tags = (TextView)listing_item_view.findViewById(R.id.listing_tags);
        TextView timings = (TextView)listing_item_view.findViewById(R.id.listing_timings);

        HashMap<String, String> data = getItem(position);
        String titleString = data.get("title");
        String tagsString = "Tags: " + data.get("tags").trim().replace(" ",", ");
        String timingsString = data.get("timings");

        title.setText(titleString);
        tags.setText(tagsString);
        timings.setText(timingsString);

        return listing_item_view;
    }
}
