package kkmp.sharktank;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by kchugh on 3/5/2017 at 1:05 PM
 */

public class R_Submit_Info2 extends AppCompatActivity {

    private EditText email, phone, birthday, gender, address;
    private Bundle bundle;

    private final static String EMAIL = "email";
    private final static String PHONE = "phone";
    private final static String ADDRESS = "address";
    private final static String GENDER  = "gender";
    private final static String BIRTHDAY = "birthday";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_submit_info2);

        final Intent rootIntent = super.getIntent();
        bundle = rootIntent.getBundleExtra("bundle");

        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        birthday = (EditText) findViewById(R.id.birthday);
        gender = (EditText) findViewById(R.id.gender);
        address = (EditText) findViewById(R.id.address);
    }

    public void clickedButton_next(View view){
        toastS("Storing Data...");
        bundle.putString(EMAIL, email.getText().toString().trim());
        bundle.putString(PHONE, phone.getText().toString().trim());
        bundle.putString(ADDRESS, address.getText().toString().trim());
        bundle.putString(GENDER, gender.getText().toString().trim());
        bundle.putString(BIRTHDAY, birthday.getText().toString().trim());
        final Intent intent = new Intent(this, R_Submit_Emergency_Info.class);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
        finish();
    }

    private void toastS(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
