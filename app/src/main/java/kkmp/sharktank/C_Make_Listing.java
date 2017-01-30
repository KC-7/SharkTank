package kkmp.sharktank;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kchugh on 1/26/2017 at 11:51 PM
 */

public class C_Make_Listing extends AppCompatActivity {

    private final static String API = "https://api.github.com/repos/KC-7/CarePear-Data/contents/";

    private static final String TITLE = "title";
    private static final String TIMINGS = "timings";
    private static final String COMMENTS = "comments";
    private static final String TAGS = "tags";
    private static final String CODE = "code";

    private CheckBox groceries, medicine, meals, other;
    private EditText title, timings, comments;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_make_listing);

        groceries = (CheckBox)findViewById(R.id.groceries_box);
        medicine = (CheckBox)findViewById(R.id.medicine_box);
        meals = (CheckBox)findViewById(R.id.meals_box);
        other = (CheckBox)findViewById(R.id.other_box);

        title = (EditText)findViewById(R.id.title_field);
        timings = (EditText)findViewById(R.id.timings_field);
        comments = (EditText)findViewById(R.id.comments_field);
    }

    public void clickedButton_post(View view) {

        String tags = "";
        CheckBox[] boxes = {groceries, medicine, meals, other};
        for (CheckBox c : boxes) {
            if (c.isChecked()) {
                String name = c.getText().toString().trim();
                tags += (name + " ");
            }
        }
        tags = tags.trim();

        final JSONObject listingFile = new JSONObject();
        try {
            String titleString = title.getText().toString().trim();
            String timingsString = timings.getText().toString().trim();
            String commentsString = comments.getText().toString().trim();
            final int code = Integer.parseInt(String.valueOf((titleString + timingsString + commentsString + tags).hashCode()).substring(0, 6));

            listingFile.put(TITLE, titleString);
            listingFile.put(TIMINGS, timingsString);
            listingFile.put(COMMENTS, commentsString);
            listingFile.put(TAGS, tags);
            listingFile.put(CODE, code);
            listingFile.put("username", getSharedPreferences("session", Context.MODE_PRIVATE).getString("username", "user"));

            final String listingFileString = listingFile.toString(2);
            new listingFileTask().execute(API + "listing/listings/" + code, listingFileString);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class addListingToListTask extends AsyncTask<String, Void, String> {

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
                    requestParams.put("message", "Registered listing " + params[3]);
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
        protected void onPostExecute(String listDetails) {
            toastL("Posted listing!");
        }
    }

    private class getListShaAndContent extends AsyncTask<String, Void, String> {

        private String listingFileString;
        private getListShaAndContent(String listing) {
            listingFileString = listing;
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                final URL url = new URL(params[0]);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

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
                JSONArray listJson = new JSONArray(list);       // current list
                JSONObject listingFile = new JSONObject(listingFileString);     // listing
                listJson.put(listingFile.getString(CODE));          // puts listing code into list
                String updatedContent = listJson.toString(2);       // gets new list's content

                toastS("Registering...");
                new addListingToListTask().execute(API + "listing/list", sha, updatedContent, listingFile.getString(CODE));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class listingFileTask extends AsyncTask<String, Void, String> {

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
                    requestParams.put("message", "Created  listing: " + new JSONObject(params[1]).getString(CODE));
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
                    return params[1];   // returns listingFileString
                } else {
                    System.out.println(responseCode + " " + connection.getResponseMessage());
                }

            }  catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String listingFileString) {
            new getListShaAndContent(listingFileString).execute(API + "listing/list");
        }
    }

    private void toastL(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void toastS(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
