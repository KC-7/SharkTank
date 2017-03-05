package kkmp.sharktank;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * Created by kchugh on 1/31/2017 at 2:00 AM
 */

public class R_RateCarepear extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_ratecarepear);
    }

    public void clickedButton_submit(View view) {
        Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
        finish();
    }

}
