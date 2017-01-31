package kkmp.sharktank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by kchugh on 1/22/2017 at 11:18 PM
 */

public class R_Dashboard extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_dashboard);

        SharedPreferences session = getSharedPreferences("session", Context.MODE_PRIVATE);
        TextView hello = (TextView)findViewById(R.id.hello_text);
        hello.setText("Hello, " + session.getString("username", "user"));
    }

    public void clickedButton_makeRequest(View view) {
        final Intent intent = new Intent(this, R_Make_Request.class);
        startActivity(intent);
    }

    public void clickedButton_browseListings(View view) {
        final Intent intent = new Intent(this, R_Browse_Listing.class);
        startActivity(intent);
    }

}
