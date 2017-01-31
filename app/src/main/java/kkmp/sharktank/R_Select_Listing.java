package kkmp.sharktank;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by kchugh on 1/31/2017 at 12:50 AM
 */

public class R_Select_Listing extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_select_listing);

        final Intent intent = getIntent();
        final HashMap<String, String> listingMap = (HashMap<String, String>)intent.getSerializableExtra("listingMap");

        TextView title = (TextView)findViewById(R.id.listing_title);
        TextView tags = (TextView)findViewById(R.id.listing_tags);
        TextView timings = (TextView)findViewById(R.id.listing_timings);
        TextView comments = (TextView)findViewById(R.id.listing_comments);

        String titleString = listingMap.get("title");
        String tagsString = "Tags: " + listingMap.get("tags").trim().replace(" ",", ");
        String timingsString = "Timings: " + listingMap.get("timings");
        String commentsString = "Details: " + listingMap.get("comments");

        title.setText(titleString);
        tags.setText(tagsString);
        timings.setText(timingsString);
        comments.setText(commentsString);
    }

}
