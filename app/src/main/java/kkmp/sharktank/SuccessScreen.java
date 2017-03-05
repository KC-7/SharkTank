package kkmp.sharktank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by kchugh on 1/31/2017 at 2:45 AM
 */

public class SuccessScreen extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.successscreen);
    }

    public void clickedButton_dashboard(View view) {
        Intent intent;
        SharedPreferences session = getSharedPreferences("session", Context.MODE_PRIVATE);
        switch (session.getString("type", "recipient")) {
            case "recipient":
                intent = new Intent(this, R_Dashboard.class);
                startActivity(intent);
                break;
            case "caregiver":
                intent = new Intent(this, C_Dashboard.class);
                startActivity(intent);
                break;
        }

    }

}
