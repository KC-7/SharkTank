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
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends AppCompatActivity {

    EditText username_field, password_field;

    private final static String API = "https://api.github.com/repos/KC-7/CarePear-Data/contents/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        username_field = (EditText) findViewById(R.id.username_field);
        password_field = (EditText) findViewById(R.id.password_field);

        SharedPreferences session = getSharedPreferences("session", Context.MODE_PRIVATE);
        if (session.getBoolean("loggedIn", false)) {
            final String username = session.getString("username", "user");
            final String type = session.getString("type", "recipient");
            final String password = session.getString("password", "pass");
            try {
                final JSONObject acct = new JSONObject();
                acct.put("username", username);
                acct.put("type", type);
                acct.put("password", password);

                handleAccountEntry(acct, true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void clickedButton_loginToAcct(View view) {
        new accountTask().execute(API + "account/list");
    }

    private void handleAccountEntry(JSONObject list, boolean overrideCheck) {
        System.out.println(overrideCheck);
        String username_entry = username_field.getText().toString().trim();

        // If the list of accounts contains the username_entry
        if (overrideCheck || list.has(username_entry)) {

            String password_entry = password_field.getText().toString().trim();

            try {
                if (overrideCheck || list.getJSONObject(username_entry).getString("password").equals(password_entry)) {
                    toastS("Logging in...");
                    final SharedPreferences session = getSharedPreferences("session", Context.MODE_PRIVATE);
                    String type = overrideCheck ? session.getString("type", "") : list.getJSONObject(username_entry).getString("type");
                    switch (type) {
                        case "caregiver":
                            Core.loginAsCaregiver(session, overrideCheck ? session.getString("username", null) : username_entry, this);
                            break;
                        case "recipient":
                            Core.loginAsRecipient(session, overrideCheck ? session.getString("username", null) : username_entry, this);
                            break;
                        default:
                            break;
                    }
                } else {
                    toastL("Wrong password.");
                }
            } catch (JSONException e) {
                toastL("ERROR: User Existent, Password Nonexistent.");
                e.printStackTrace();
            }

        } else {
            toastL("Wrong username.");
        }
    }

    private class accountTask extends AsyncTask<String, Void, String> {

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
                    toastL("ERROR: Connection code " + connection.getResponseCode());
                }

            } catch (IOException e) {
                toastL("ERROR: Connection failure.");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                final JSONObject listDetails = new JSONObject(response);

                String encodedContent = listDetails.getString("content").replace("\n","");
                String list = new String(Base64.decode(encodedContent, Base64.DEFAULT));
                JSONObject listJson = new JSONObject(list);

                handleAccountEntry(listJson, false);
            } catch (JSONException e) {
                toastL("ERROR: Retrieval parsing error.");
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
