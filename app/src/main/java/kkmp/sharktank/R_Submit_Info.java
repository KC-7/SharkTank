package kkmp.sharktank;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by prad2_is_awsome on 1/22/2017.
 */

public class R_Submit_Info extends AppCompatActivity {

    private final static String FIRSTNAME = "firstname";
    private final static String LASTNAME = "lastname";
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";
    private final static String EMAIL = "email";
    private final static String PHONE = "phone";
    private final static String ADDRESS = "address";
    private final static String GENDER  = "gender";
    private final static String BIRTHDAY = "birthday";

    private final static String API = "https://api.github.com/repos/KC-7/CarePear-Data/contents/";

    private EditText firstname_field, lastname_field, username_field, password_field;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_submit_info);

        firstname_field = (EditText)findViewById(R.id.firstname);
        lastname_field = (EditText)findViewById(R.id.lastname);
        username_field = (EditText)findViewById(R.id.username);
        password_field = (EditText)findViewById(R.id.password);
    }

    public void clickedButton_next(View view){
        new R_Submit_Info.usernameTask().execute(API + "account/list");
    }

    private void goToR_Submit_info2() {
        final Bundle bundle = new Bundle();
        bundle.putString(FIRSTNAME, firstname_field.getText().toString().trim());
        bundle.putString(LASTNAME, lastname_field.getText().toString().trim());
        bundle.putString(USERNAME, username_field.getText().toString().trim());
        bundle.putString(PASSWORD, password_field.getText().toString().trim());

        final Intent intent = new Intent(this, R_Submit_Info2.class);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
        finish();
    }

    private class usernameTask extends AsyncTask<String, Void, String> {

        // Gets list of accounts
        @Override
        protected String doInBackground(String... urlString) {
            try {

                final URL url = new URL(urlString[0]);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    return Core.readStream(connection.getInputStream());
                } else {
                    throw new AssertionError();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        // Publishes account info if username is unique
        @Override
        protected void onPostExecute(String response) {
            try {
                final JSONObject listDetails = new JSONObject(response);
                String encodedContent = listDetails.getString("content").replace("\n","");
                String list = new String(Base64.decode(encodedContent, Base64.DEFAULT));
                final JSONObject listOfAccounts = new JSONObject(list);
                final String username = username_field.getText().toString().trim();
                if (listOfAccounts.has(username)) {
                    toastL("Username already taken.");
                } else {
                    toastS("Storing Data...");
                    goToR_Submit_info2();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void toastL(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void toastS(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
