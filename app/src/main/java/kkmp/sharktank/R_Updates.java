package kkmp.sharktank;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class R_Updates extends AppCompatActivity {

    private final static String API = "https://api.github.com/repos/KC-7/CarePear-Data/contents/";
    private TextView status, name_age_gender, contact_info, yourCaregiverIs, youCanContactAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_updates);

        status = (TextView) findViewById(R.id.status);
        name_age_gender = (TextView) findViewById(R.id.name_age_gender);
        contact_info = (TextView) findViewById(R.id.contact_info);
        yourCaregiverIs = (TextView)findViewById(R.id.yourCaregiverIs);
        youCanContactAt = (TextView) findViewById(R.id.youCanContactAt);

        name_age_gender.setVisibility(View.INVISIBLE);
        contact_info.setVisibility(View.INVISIBLE);
        yourCaregiverIs.setVisibility(View.INVISIBLE);
        youCanContactAt.setVisibility(View.INVISIBLE);

        new getAccountRegistry().execute(API + "account/list");
    }

    private void processCaregiverFile(JSONObject caregiverFile) {

        try {
            String fnString = caregiverFile.getString("firstname");
            String lnString = caregiverFile.getString("lastname");
            String age = caregiverFile.getString("birthday");

            String nagString = fnString + " " + lnString + ", " + age;
            name_age_gender.setText(nagString);

            String contactInfoString = "";
            if (!caregiverFile.getString("email").isEmpty()) {
                contactInfoString += caregiverFile.getString("email") + "\n";
            }
            if (!caregiverFile.getString("phone").isEmpty()) {
                contactInfoString += caregiverFile.getString("phone") + "\n";
            }
            if (!caregiverFile.getString("address").isEmpty()) {
                contactInfoString += caregiverFile.getString("address") + "\n";
            }
            contact_info.setText(contactInfoString);

            name_age_gender.setVisibility(View.VISIBLE);
            contact_info.setVisibility(View.VISIBLE);
            yourCaregiverIs.setVisibility(View.VISIBLE);
            youCanContactAt.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private class getCaregiverFileTask extends AsyncTask<String, Void, String> {

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
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                final JSONObject fileDetails = new JSONObject(response);
                String encodedContent = fileDetails.getString("content").replace("\n","");
                String file = new String(Base64.decode(encodedContent, Base64.DEFAULT));
                JSONObject fileJson = new JSONObject(file);

                processCaregiverFile(fileJson);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class getAccountRegistry extends AsyncTask<String, Void, String> {

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

                SharedPreferences session = getSharedPreferences("session", Context.MODE_PRIVATE);
                String myUsername = session.getString("username", "user");
                JSONObject myAccountRegistry = listJson.getJSONObject(myUsername);
                if (myAccountRegistry.has("carepear")) {
                    String carepearC = myAccountRegistry.getString("carepear");
                    status.setText("Your request has been accepted!");
                    new getCaregiverFileTask().execute(API + "account/caregiver/" + carepearC);
                } else {
                    status.setText("None of your requests have been accepted yet.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
