package kkmp.sharktank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by kchugh on 1/26/2017 at 10:56 PM
 */

public class C_Dashboard extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_dashboard);

        SharedPreferences session = getSharedPreferences("session", Context.MODE_PRIVATE);
        TextView hello = (TextView)findViewById(R.id.hello);
        hello.setText("Hello, " + session.getString("username", "user"));
    }

    public void clickedButton_makeListing(View view) {
        final Intent intent = new Intent(this, C_Make_Listing.class);
        startActivity(intent);
    }

    public void clickedButton_browseRequest(View view) {
        final Intent intent = new Intent(this, C_Browse_Request.class);
        startActivity(intent);
    }

    public void clickedButton_logout(View view) {
        final Intent intent = new Intent(this, Main.class);
        startActivity(intent);
        finish();
    }

}