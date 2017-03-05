package kkmp.sharktank;

import android.content.Context;
import android.content.SharedPreferences;
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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by prad2_is_awsome on 1/22/2017.
 */

public class R_Submit_Emergency_Info extends AppCompatActivity {

    private final static String FIRSTNAME = "firstname";
    private final static String LASTNAME = "lastname";
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";
    private final static String EMAIL = "email";
    private final static String PHONE = "phone";
    private final static String ADDRESS = "address";
    private final static String GENDER  = "gender";
    private final static String BIRTHDAY = "birthday";

    private final static String E_FIRSTNAME = "emergency-firstname";
    private final static String E_LASTNAME = "emergency-lastname";
    private final static String E_EMAIL = "emergency-email";
    private final static String E_PHONE = "emergency-phone";

    private final static String API = "https://api.github.com/repos/KC-7/CarePear-Data/contents/";

    private Bundle bundle;
    private JSONObject accountFile;

    EditText e_firstname_field, e_lastname_field, e_email_field, e_phone_field;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_submit_emergency_info);

        e_firstname_field = (EditText)findViewById(R.id.firstname_E);
        e_lastname_field = (EditText)findViewById(R.id.lastname_E);
        e_email_field = (EditText)findViewById(R.id.email_E);
        e_phone_field = (EditText)findViewById(R.id.phone_E);

        if (getIntent().hasExtra("bundle")) {
            bundle = getIntent().getBundleExtra("bundle");
        }
    }

    private void goToDashboard() {
        SharedPreferences session = getSharedPreferences("session", Context.MODE_PRIVATE);
        Core.loginAsRecipient(session, accountFile, this);
        finish();
    }

    public void clickedButton_finish(View view) {

        JSONObject accountFile = new JSONObject();
        try {
            accountFile.put(FIRSTNAME, bundle.getString(FIRSTNAME));
            accountFile.put(LASTNAME, bundle.getString(LASTNAME));
            accountFile.put(USERNAME, bundle.getString(USERNAME));
            accountFile.put(PASSWORD, bundle.getString(PASSWORD));
            accountFile.put(EMAIL, bundle.getString(EMAIL));
            accountFile.put(PHONE, bundle.getString(PHONE));
            accountFile.put(ADDRESS, bundle.getString(ADDRESS));
            accountFile.put(GENDER, bundle.getString(GENDER));
            accountFile.put(BIRTHDAY, bundle.getString(BIRTHDAY));

            accountFile.put(E_FIRSTNAME, e_firstname_field.getText().toString().trim());
            accountFile.put(E_LASTNAME, e_lastname_field.getText().toString().trim());
            accountFile.put(E_EMAIL, e_email_field.getText().toString().trim());
            accountFile.put(E_PHONE, e_phone_field.getText().toString().trim());

            String accountFileString = accountFile.toString(4);
            new accountFileTask().execute(API + "account/recipient/" + bundle.getString(USERNAME).trim(), accountFileString);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.accountFile = accountFile;
    }

    private class addAccountToListTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {

                final URL url = new URL(params[0]);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type:", "application/json");

                String token = "c2341499852a34c450" + "fab7a962b8efda429c1522" + ":x-oauth-basic";
                String authString = "Basic " + Base64.encodeToString(token.getBytes(), Base64.DEFAULT);
                connection.setRequestProperty("Authorization", authString);

                JSONObject requestParams = new JSONObject();
                try {
                    requestParams.put("message", "Registered " + params[3]);
                    requestParams.put("content", Base64.encodeToString(params[2].getBytes(), Base64.DEFAULT).replace("\n", ""));
                    requestParams.put("sha", params[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(requestParams.toString());
                out.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    Core.readStream(connection.getInputStream());
                    return params[1];
                } else {
                    System.out.println(responseCode + " " + connection.getResponseMessage());
                }

            }  catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String accountDetails) {
            goToDashboard();
        }
    }

    private class getListShaAndContent extends AsyncTask<String, Void, String> {

        public String accountFileString;
        public getListShaAndContent(String acct) {
            accountFileString = acct;
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                final URL url = new URL(params[0]);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                String token = "c2341499852a34c450" + "fab7a962b8efda429c1522" + ":x-oauth-basic";
                String authString = "Basic " + Base64.encodeToString(token.getBytes(), Base64.DEFAULT);
                connection.setRequestProperty("Authorization", authString);

                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    return Core.readStream(connection.getInputStream());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                final JSONObject listDetails = new JSONObject(response);
                String sha = listDetails.getString("sha");

                String encodedContent = listDetails.getString("content").replace("\n","");
                String list = new String(Base64.decode(encodedContent, Base64.DEFAULT));
                JSONObject listJson = new JSONObject(list);
                JSONObject accountFile = new JSONObject(accountFileString);
                JSONObject passAndType = new JSONObject();
                passAndType.put(PASSWORD, accountFile.getString(PASSWORD));
                passAndType.put("type", "recipient");
                listJson.put(accountFile.getString(USERNAME), passAndType);
                String updatedContent = listJson.toString(4);

                toastS("Registering...");
                new addAccountToListTask().execute(API + "account/list", sha, updatedContent, accountFile.getString(USERNAME));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class accountFileTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {

                final URL url = new URL(params[0]);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type:", "application/json");

                String token = "c2341499852a34c450" + "fab7a962b8efda429c1522" + ":x-oauth-basic";
                String authString = "Basic " + Base64.encodeToString(token.getBytes(), Base64.DEFAULT);
                connection.setRequestProperty("Authorization", authString);

                JSONObject requestParams = new JSONObject();
                try {
                    requestParams.put("message", "Created recipient account: " + new JSONObject(params[1]).getString(USERNAME));
                    requestParams.put("content", Base64.encodeToString(params[1].getBytes(), Base64.DEFAULT).replace("\n", ""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(requestParams.toString());
                out.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    Core.readStream(connection.getInputStream());
                    return params[1];   // returns accountFileString
                } else {
                    System.out.println(responseCode + " " + connection.getResponseMessage());
                }

            }  catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String accountFileString) {
            new getListShaAndContent(accountFileString).execute(API + "account/list");
        }
    }


    private void toastL(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void toastS(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
