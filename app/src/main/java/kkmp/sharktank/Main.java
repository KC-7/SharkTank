package kkmp.sharktank;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void clickedButton_caregiver(View view) {
        final Intent intent = new Intent(this, C_Submit_Info.class);
        startActivity(intent);
    }

    public void clickedButton_recipient(View view) {

    }

    public void clickedButton_login(View view) {

    }

}
