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
 * Created by kchugh on 1/31/2017 at 12:32 AM
 */

public class C_Select_Request extends AppCompatActivity {

    private final static String API = "https://api.github.com/repos/KC-7/CarePear-Data/contents/";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_select_request);

        final Intent intent = getIntent();
        final HashMap<String, String> requestMap = (HashMap<String, String>)intent.getSerializableExtra("requestMap");

        TextView title = (TextView)findViewById(R.id.request_title);
        TextView tags = (TextView)findViewById(R.id.request_tags);
        TextView timings = (TextView)findViewById(R.id.request_timings);
        TextView comments = (TextView)findViewById(R.id.request_comments);

        String titleString = requestMap.get("title");
        String tagsString = "Tags: " + requestMap.get("tags").trim().replace(" ",", ");
        String timingsString = "Timings: " + requestMap.get("timings");
        String commentsString = "Details: " + requestMap.get("comments");

        title.setText(titleString);
        tags.setText(tagsString);
        timings.setText(timingsString);
        comments.setText(commentsString);

        String usernameString = requestMap.get("username");
        new getRecipientFileTask().execute(API + "account/recipient/" + usernameString);
    }

    public void clickedButton_select(View view) {
        final Intent intent = new Intent(this, SuccessScreen.class);
        startActivity(intent);
    }

    private void processRecipientFile(JSONObject recipientFile) {

        try {
            String fnString = recipientFile.getString("firstname");
            String lnString = recipientFile.getString("lastname");
            String age = recipientFile.getString("birthday");
            String gender = recipientFile.getString("gender");

            String nagString = fnString + " " + lnString + ", " + age + ", " + gender;
            TextView nag = (TextView)findViewById(R.id.name_age_gender);
            nag.setText(nagString);

            String contactInfoString = "";
            if (!recipientFile.getString("email").isEmpty()) {
                contactInfoString += recipientFile.getString("email") + "\n";
            }
            if (!recipientFile.getString("phone").isEmpty()) {
                contactInfoString += recipientFile.getString("phone") + "\n";
            }
            if (!recipientFile.getString("address").isEmpty()) {
                contactInfoString += recipientFile.getString("address") + "\n";
            }

            TextView info = (TextView)findViewById(R.id.contact_info);
            info.setText(contactInfoString);
        } catch (JSONException e) {
            e.printStackTrace();
            toastL("ERROR: Recipient file retrieval error.");
        }

    }

    private class getRecipientFileTask extends AsyncTask<String, Void, String> {

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
                final JSONObject fileDetails = new JSONObject(response);
                String encodedContent = fileDetails.getString("content").replace("\n","");
                String file = new String(Base64.decode(encodedContent, Base64.DEFAULT));
                JSONObject fileJson = new JSONObject(file);

                processRecipientFile(fileJson);

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
