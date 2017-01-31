package kkmp.sharktank;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by kchugh on 1/31/2017 at 12:32 AM
 */

public class C_Select_Request extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_select_request);

        final Intent intent = getIntent();
        final HashMap<String, String> requestMap = (HashMap<String, String>)intent.getSerializableExtra("requestMap");

        TextView title = (TextView)findViewById(R.id.request_title);
        TextView tags = (TextView)findViewById(R.id.request_tags);
        TextView timings = (TextView)findViewById(R.id.request_timings);
        TextView comments = (TextView)findViewById(R.id.request_comments);

        String titleString = requestMap.get("title");
        String tagsString = "Tags: " + requestMap.get("tags").trim().replace(" ",", ");
        String timingsString = "Timings: " + requestMap.get("timings");
        String commentsString = "Details: " + requestMap.get("comments");

        title.setText(titleString);
        tags.setText(tagsString);
        timings.setText(timingsString);
        comments.setText(commentsString);
    }

}
