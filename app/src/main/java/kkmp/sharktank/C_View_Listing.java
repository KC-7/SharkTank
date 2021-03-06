package kkmp.sharktank;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by kchugh on 1/31/2017 at 2:34 PM
 */

public class C_View_Listing extends AppCompatActivity {

    private final static String API = "https://api.github.com/repos/KC-7/CarePear-Data/contents/";
    private boolean backLaunchesDashboard = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_view_listing);

        final Intent intent = getIntent();
        final HashMap<String, String> listingMap = (HashMap<String, String>)intent.getSerializableExtra("listingMap");
        if (intent.hasExtra("backLaunchesDashboard")) {
            backLaunchesDashboard = intent.getBooleanExtra("backLaunchesDashboard", true);
        }

        TextView title = (TextView)findViewById(R.id.listing_title);
        TextView tags = (TextView)findViewById(R.id.listing_tags);
        TextView timings = (TextView)findViewById(R.id.listing_timings);
        TextView comments = (TextView)findViewById(R.id.listing_comments);

        String titleString = listingMap.get("title");
        String tagsString = "Tags: " + listingMap.get("tags").trim().replace(" ",", ");
        String timingsString = "Timings: " + listingMap.get("timings");
        String commentsString = "Details: " + listingMap.get("comments");

        title.setText(titleString);
        tags.setText(tagsString);
        timings.setText(timingsString);
        comments.setText(commentsString);

        String usernameString = listingMap.get("username");
        new getCaregiverFileTask().execute(API + "account/caregiver/" + usernameString);
    }

    public void clickedButton_back(View view) {
        if (backLaunchesDashboard) {
            final Intent intent = new Intent(this, C_Dashboard.class);
            startActivity(intent);
        }
        finish();
    }

    private void processCaregiverFile(JSONObject caregiverFile) {

        try {
            String fnString = caregiverFile.getString("firstname");
            String lnString = caregiverFile.getString("lastname");
            String age = caregiverFile.getString("birthday");

            String nagString = fnString + " " + lnString + ", " + age;
            TextView nag = (TextView)findViewById(R.id.name_age_gender);
            nag.setText(nagString);

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

            TextView info = (TextView)findViewById(R.id.contact_info);
            info.setText(contactInfoString);
        } catch (JSONException e) {
            e.printStackTrace();
            toastL("ERROR: Caregiver file retrieval error.");
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
                final JSONObject fileDetails = new JSONObject(response);
                String encodedContent = fileDetails.getString("content").replace("\n","");
                String file = new String(Base64.decode(encodedContent, Base64.DEFAULT));
                JSONObject fileJson = new JSONObject(file);

                processCaregiverFile(fileJson);

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
