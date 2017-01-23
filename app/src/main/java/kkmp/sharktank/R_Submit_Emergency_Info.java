package kkmp.sharktank;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
/**
 * Created by prad2_is_awsome on 1/22/2017.
 */

public class R_Submit_Emergency_Info extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_submit_emergency_info);
    }

    public void clickedButton_finish(View view){
        Intent intent = new Intent(this, R_Dashboard.class);
        startActivity(intent);
    }
}
