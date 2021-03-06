package kkmp.sharktank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by kchugh on 1/22/2017 at 11:18 PM
 */

public class R_Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_dashboard);

        SharedPreferences session = getSharedPreferences("session", Context.MODE_PRIVATE);
        TextView hello = (TextView)findViewById(R.id.hello_text);
        hello.setText("Hello, " + session.getString("firstname", "user"));
    }

    public void clickedButton_makeRequest(View view) {
        final Intent intent = new Intent(this, R_Make_Request.class);
        startActivity(intent);
    }

    public void clickedButton_browseListings(View view) {
        final Intent intent = new Intent(this, R_Browse_Listing.class);
        startActivity(intent);
    }

    public void clickedButton_rateMyCarepear(View view) {
        final Intent intent = new Intent(this, R_RateCarepear.class);
        startActivity(intent);
    }

    public void clickedButton_logout(View view) {
        final Intent intent = new Intent(this, Main.class);
        startActivity(intent);
        finish();
        Core.logout(getSharedPreferences("session", Context.MODE_PRIVATE), this);
    }

    public void clickedButton_911(View view) {
        try {
            final Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", "911", null));
            startActivity(intent);
        } catch (SecurityException e) {
            final Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "911", null));
            startActivity(intent);
        }
    }

    public void clickedButton_emergency(View view) {
        final Intent intent = new Intent(this, R_Emergency.class);
        startActivity(intent);
    }

    public void clickedButton_updates(View view) {
        final Intent intent = new Intent(this, R_Updates.class);
        startActivity(intent);
    }

}
