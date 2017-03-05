package kkmp.sharktank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
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
 * Created by kchugh on 1/31/2017 at 12:50 AM
 */

public class R_Select_Listing extends AppCompatActivity {

    private final static String API = "https://api.github.com/repos/KC-7/CarePear-Data/contents/";

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

    private String sha;
    private JSONObject caregiverFile;
    private String code;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_select_listing);

        final Intent intent = getIntent();
        final HashMap<String, String> listingMap = (HashMap<String, String>)intent.getSerializableExtra("listingMap");

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

    public void clickedButton_select(View view) {
        final Intent intent = new Intent(this, SuccessScreen.class);
        startActivity(intent);

        new getAccountListShaAndContent().execute(API + "account/list");
        code = ((HashMap<String, String>)getIntent().getSerializableExtra("listingMap")).get("code");
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
                    requestParams.put("message", "Carepeared " + params[3] + " (r)");
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
            new getListShaAndContent(code).execute(API + "listing/list");
        }
    }

    private class getAccountListShaAndContent extends AsyncTask<String, Void, String> {
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
                sha = listDetails.getString("sha");
                final  String encodedContent = listDetails.getString("content").replace("\n","");
                final  String list = new String(Base64.decode(encodedContent, Base64.DEFAULT));

                final SharedPreferences session = getSharedPreferences("session", Context.MODE_PRIVATE);
                final String myRecipientUsername = session.getString("username", "user");

                JSONObject listJson = new JSONObject(list);
                JSONObject caregiverSection = (JSONObject) listJson.get(caregiverFile.getString(USERNAME));
                caregiverSection.put("carepear", myRecipientUsername);
                listJson.put(caregiverFile.getString(USERNAME), caregiverSection);

                String updatedContent = listJson.toString(4);

                new addAccountToListTask().execute(API + "account/list", sha, updatedContent, caregiverFile.getString(USERNAME));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class removeListingFromListTask extends AsyncTask<String, Void, String> {

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
                    requestParams.put("message", "Unregistered listing " + params[3]);
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

        }
    }

    private class getListShaAndContent extends AsyncTask<String, Void, String> {

        private String CODE;
        public getListShaAndContent(String code) {
            CODE = code;
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
                JSONArray newListJson = new JSONArray();        // new list
                for (int i = 0; i < listJson.length(); i++) {   // copy list to new list, except for the code
                    if (!listJson.getString(i).equals(CODE)) {
                        newListJson.put(listJson.getString(i));
                    }
                }

                String updatedContent = newListJson.toString(4);       // gets new list's content

                new removeListingFromListTask().execute(API + "listing/list", sha, updatedContent, CODE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
                caregiverFile = fileJson;
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
