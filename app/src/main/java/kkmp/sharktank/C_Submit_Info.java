package kkmp.sharktank;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class C_Submit_Info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_submit_info);
    }

    public void clickedButton_finish(View view) {
        final Intent intent = new Intent(this, C_Submitted_Info.class);
        startActivity(intent);
    }
}
