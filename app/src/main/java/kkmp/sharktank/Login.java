package kkmp.sharktank;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    public void clickedButton_login(View view) {
        // check if account is caregiver, recipient, or doesn’t exist
        String accountType = "asdjlasdja";
        switch (accountType) {
            case "caregiver":
            case "recipient":
            case "nonexistent":
        }

    }

}
