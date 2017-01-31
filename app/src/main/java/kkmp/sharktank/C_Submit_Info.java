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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class C_Submit_Info extends AppCompatActivity {

    private final static String FIRSTNAME = "firstname";
    private final static String LASTNAME = "lastname";
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";
    private final static String EMAIL = "email";
    private final static String PHONE = "phone";
    private final static String ADDRESS = "address";
    private final static String SSN = "ssn";
    private final static String BIRTHDAY = "birthday";

    private final static String API = "https://api.github.com/repos/KC-7/CarePear-Data/contents/";

    private EditText firstname_field, lastname_field, username_field, password_field, email_field,
            phone_field, address_field, ssn_field, birthday_field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_submit_info);

        firstname_field = (EditText)findViewById(R.id.firstname_field);
        lastname_field = (EditText)findViewById(R.id.lastname_field);
        username_field = (EditText)findViewById(R.id.username_field);
        password_field = (EditText)findViewById(R.id.password_field);
        email_field = (EditText)findViewById(R.id.email_field);
        phone_field = (EditText)findViewById(R.id.phone_field);
        address_field = (EditText)findViewById(R.id.address_field);
        ssn_field = (EditText)findViewById(R.id.ssn_field);
        birthday_field = (EditText)findViewById(R.id.birthday_field);
    }

    public void clickedButton_finish(View view) {
        new usernameTask().execute(API + "account/list");
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
            final Intent intent = new Intent();
            intent.setClassName("kkmp.sharktank", "kkmp.sharktank.C_Submitted_Info");
            startActivity(intent);
            finish();
        }
    }

    private class getListShaAndContent extends AsyncTask<String, Void, String> {

        private String accountFileString;
        private getListShaAndContent(String acct) {
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
                passAndType.put("type", "caregiver");
                listJson.put(accountFile.getString(USERNAME), passAndType);
                String updatedContent = listJson.toString(2);

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
                    requestParams.put("message", "Created caregiver account: " + new JSONObject(params[1]).getString(USERNAME));
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

    // Compiles account info and executes the file task
    private void publishAccountFile() {
        JSONObject accountFile = new JSONObject();
        try {
            accountFile.put(FIRSTNAME, firstname_field.getText().toString().trim());
            accountFile.put(LASTNAME, lastname_field.getText().toString().trim());
            accountFile.put(USERNAME, username_field.getText().toString().trim());
            accountFile.put(PASSWORD, password_field.getText().toString().trim());
            accountFile.put(EMAIL, email_field.getText().toString().trim());
            accountFile.put(PHONE, phone_field.getText().toString().trim());
            accountFile.put(ADDRESS, address_field.getText().toString().trim());
            accountFile.put(SSN, ssn_field.getText().toString().trim());
            accountFile.put(BIRTHDAY, birthday_field.getText().toString().trim());

            String accountFileString = accountFile.toString(2);
            new accountFileTask().execute(API + "account/caregiver/" + username_field.getText().toString().trim(), accountFileString);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class usernameTask extends AsyncTask<String, Void, String> {

        // Gets list of accounts
        @Override
        protected String doInBackground(String... urlString) {
            try {

                final URL url = new URL(urlString[0]);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                String token = "c2341499852a34c450" + "fab7a962b8efda429c1522" + ":x-oauth-basic";
                String authString = "Basic " + Base64.encodeToString(token.getBytes(), Base64.DEFAULT);
                connection.setRequestProperty("Authorization", authString);

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
                final JSONObject listJson = new JSONObject(list);
                if (listJson.has(username_field.getText().toString().trim())) {
                    toastL("Username already taken.");
                } else {
                    toastS("Storing Data...");
                    publishAccountFile();
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


