package kkmp.sharktank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class R_Emergency extends AppCompatActivity {

    private final static String E_FIRSTNAME = "emergency-firstname";
    private final static String E_LASTNAME = "emergency-lastname";
    private final static String E_EMAIL = "emergency-email";
    private final static String E_PHONE = "emergency-phone";

    private boolean runTimer = true;
    private TextView bannerText;
    private String phoneNum;
    private int seconds = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_emergency);

        final SharedPreferences session = getSharedPreferences("session", Context.MODE_PRIVATE);
        final TextView name = (TextView)findViewById(R.id.name);
        final TextView email = (TextView)findViewById(R.id.email);
        final TextView phone = (TextView)findViewById(R.id.phone);
        bannerText = (TextView)findViewById(R.id.bannerText);

        name.setText(session.getString(E_FIRSTNAME, "Emergency") + " " + session.getString(E_LASTNAME, "Contact"));
        email.setText(session.getString(E_EMAIL, ""));
        phoneNum = session.getString(E_PHONE, "");
        phone.setText(phoneNum);

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
            if (runTimer) {
                if (seconds > 0) {
                    bannerText.setText("   Calling Contact in " + seconds + "...");
                    seconds--;
                    handler.postDelayed(this, 1200);
                } else {
                    bannerText.setText("     Calling Contact...");
                    call();
                }
            }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        runTimer = false;
    }

    public void clickedButton_call(View view) {
        call();
    }

    public void clickedButton_cancel(View view) {
        runTimer = false;
        bannerText.setText("       Call Cancelled");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    private void call() {
        if (runTimer) {
            runTimer = false;
            try {
                final Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", phoneNum, null));
                startActivity(intent);
            } catch (SecurityException e) {
                final Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNum, null));
                startActivity(intent);
            }
            finish();
        }
    }
}
