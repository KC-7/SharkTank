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
 * Created by kchugh on 1/31/2017 at 12:32 AM
 */

public class C_Select_Request extends AppCompatActivity {

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

    private static String sha;
    private static JSONObject recipientFile;

    private String code;

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

        new getAccountListShaAndContent().execute(API + "account/list");
        code = ((HashMap<String, String>)getIntent().getSerializableExtra("requestMap")).get("code");
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
            new getListShaAndContent(code).execute(API + "request/list");
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
                final String sha = listDetails.getString("sha");
                final  String encodedContent = listDetails.getString("content").replace("\n","");
                final  String list = new String(Base64.decode(encodedContent, Base64.DEFAULT));

                final SharedPreferences session = getSharedPreferences("session", Context.MODE_PRIVATE);
                final String myCaregiverUsername = session.getString("username", "user");

                JSONObject listJson = new JSONObject(list);
                JSONObject recipientSection = (JSONObject) listJson.get(recipientFile.getString(USERNAME));
                recipientSection.put("carepear", myCaregiverUsername);
                listJson.put(recipientFile.getString(USERNAME), recipientSection);

                String updatedContent = listJson.toString(4);

                new addAccountToListTask().execute(API + "account/list", sha, updatedContent, recipientFile.getString(USERNAME));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class removeRequestFromListTask extends AsyncTask<String, Void, String> {

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
                    requestParams.put("message", "Unregistered request " + params[3]);
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
                sha = listDetails.getString("sha");
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

                new removeRequestFromListTask().execute(API + "request/list", sha, updatedContent, CODE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
                recipientFile = fileJson;
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
