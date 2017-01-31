package kkmp.sharktank;

import android.content.Context;
import android.content.Intent;
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
    }

    public void clickedButton_loginToAcct(View view) {
        new accountTask().execute(API + "account/list");
    }

    private void handleAccountEntry(JSONObject list) {
        String username_entry = username_field.getText().toString().trim();

        // If the list of accounts contains the username_entry
        if (list.has(username_entry)) {

            String password_entry = password_field.getText().toString().trim();

            try {
                String pass = list.getJSONObject(username_entry).getString("password");
                if (pass.equals(password_entry)) {
                    toastS("Logging in...");
                    String type = list.getJSONObject(username_entry).getString("type");

                    SharedPreferences session = getSharedPreferences("session", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = session.edit();
                    editor.putString("username", username_entry);
                    editor.apply();

                    switch (type) {
                        case "caregiver":
                            editor.putString("type", "caregiver");
                            editor.apply();
                            Intent intent = new Intent(this, C_Dashboard.class);
                            startActivity(intent);
                            break;
                        case "recipient":
                            editor.putString("type", "recipient");
                            editor.apply();
                            intent = new Intent(this, R_Dashboard.class);
                            startActivity(intent);
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
                    throw new AssertionError(connection.getResponseCode());
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

                handleAccountEntry(listJson);
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
