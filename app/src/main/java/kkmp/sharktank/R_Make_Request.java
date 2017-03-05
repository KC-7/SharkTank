package kkmp.sharktank;

import android.content.Context;
import android.content.Intent;
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
import java.util.HashMap;

/**
 * Created by kchugh on 1/23/2017 at 10:18 AM
 */

public class R_Make_Request extends AppCompatActivity {

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
        setContentView(R.layout.r_make_request);

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

        final JSONObject requestFile = new JSONObject();
        try {
            String titleString = title.getText().toString().trim();
            String timingsString = timings.getText().toString().trim();
            String commentsString = comments.getText().toString().trim();

            String merged = titleString + timingsString + commentsString + tags + String.valueOf(System.currentTimeMillis());
            final int code = Integer.parseInt(String.valueOf(Math.abs((merged).hashCode())).substring(0, 7));

            requestFile.put(TITLE, titleString);
            requestFile.put(TIMINGS, timingsString);
            requestFile.put(COMMENTS, commentsString);
            requestFile.put(TAGS, tags);
            requestFile.put(CODE, code);
            requestFile.put("username", getSharedPreferences("session", Context.MODE_PRIVATE).getString("username", "user"));

            final String requestFileString = requestFile.toString(4);
            new requestFileTask().execute(API + "request/requests/" + code, requestFileString);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class addRequestToListTask extends AsyncTask<String, Void, String> {

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
                    requestParams.put("message", "Registered request " + params[3]);
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
            toastL("Posted request!");

            String tags = "";
            CheckBox[] boxes = {groceries, medicine, meals, other};
            for (CheckBox c : boxes) {
                if (c.isChecked()) {
                    String name = c.getText().toString().trim();
                    tags += (name + " ");
                }
            }
            tags = tags.trim();

            String titleString = title.getText().toString().trim();
            String timingsString = timings.getText().toString().trim();
            String commentsString = comments.getText().toString().trim();

            HashMap<String, String> requestMap = new HashMap<>();
            requestMap.put("title", titleString);
            requestMap.put("tags", tags);
            requestMap.put("timings", timingsString);
            requestMap.put("comments", commentsString);
            requestMap.put("username", getSharedPreferences("session", Context.MODE_PRIVATE).getString("username", "user"));

            final Intent intent = new Intent();
            intent.setClassName("kkmp.sharktank", "kkmp.sharktank.R_View_Request");
            intent.putExtra("requestMap", requestMap);
            startActivity(intent);
            finish();
        }
    }

    private class getListShaAndContent extends AsyncTask<String, Void, String> {

        private String requestFileString;
        private getListShaAndContent(String request) {
            requestFileString = request;
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
                JSONArray listJson = new JSONArray(list);       // current list
                JSONObject requestFile = new JSONObject(requestFileString);     // request
                listJson.put(requestFile.getString(CODE));          // puts request code into list
                String updatedContent = listJson.toString(4);       // gets new list's content

                toastS("Registering request...");
                new addRequestToListTask().execute(API + "request/list", sha, updatedContent, requestFile.getString(CODE));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class requestFileTask extends AsyncTask<String, Void, String> {

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
                    requestParams.put("message", "Created  request: " + new JSONObject(params[1]).getString(CODE));
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
                    return params[1];   // returns requestFileString
                } else {
                    System.out.println(responseCode + " " + connection.getResponseMessage());
                }

            }  catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String requestFileString) {
            new getListShaAndContent(requestFileString).execute(API + "request/list");
        }
    }

    private void toastL(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void toastS(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
