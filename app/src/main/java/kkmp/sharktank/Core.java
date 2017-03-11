package kkmp.sharktank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kchugh on 1/25/2017 at 4:23 PM
 */

public class Core {

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

    public static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String responseString = response.toString();
        try {
            JSONObject responseJson = new JSONObject(responseString);
            return responseJson.toString(4);
        } catch (JSONException e) {
            return responseString;
        }

    }

    static void logout(SharedPreferences session, Context context) {
        SharedPreferences.Editor editor = session.edit();
        editor.putBoolean("loggedIn", false);
        editor.apply();

        Toast.makeText(context, "Logging out...", Toast.LENGTH_SHORT).show();
    }

    private static void launchDashboard(Context context, boolean recipient) {
        final Intent intent = new Intent(context, (recipient ? R_Dashboard.class : C_Dashboard.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    static void loginAsCaregiver(SharedPreferences session, String username, Context context) {
        SharedPreferences.Editor editor = session.edit();
        editor.putString("username", username);
        editor.putString("type", "caregiver");
        editor.putBoolean("loggedIn", true);
        editor.apply();
        launchDashboard(context, false);
    }

    static void loginAsRecipient(SharedPreferences session, String username, Context context) {
        new getRecipientFileTask(session, context).execute(API + "account/recipient/" + username);
    }
    
    static void loginAsRecipient(SharedPreferences session, JSONObject recipientAccountFile, Context context) {
        try {
            SharedPreferences.Editor editor = session.edit();
            editor.putString(FIRSTNAME, recipientAccountFile.getString(FIRSTNAME));
            editor.putString(LASTNAME, recipientAccountFile.getString(LASTNAME));
            editor.putString(USERNAME, recipientAccountFile.getString(USERNAME));
            editor.putString(PASSWORD, recipientAccountFile.getString(PASSWORD));
            editor.putString(EMAIL, recipientAccountFile.getString(EMAIL));
            editor.putString(PHONE, recipientAccountFile.getString(PHONE));
            editor.putString(ADDRESS, recipientAccountFile.getString(ADDRESS));
            editor.putString(GENDER, recipientAccountFile.getString(GENDER));
            editor.putString(BIRTHDAY, recipientAccountFile.getString(BIRTHDAY));

            editor.putString(E_FIRSTNAME, recipientAccountFile.getString(E_FIRSTNAME));
            editor.putString(E_LASTNAME, recipientAccountFile.getString(E_LASTNAME));
            editor.putString(E_EMAIL, recipientAccountFile.getString(E_EMAIL));
            editor.putString(E_PHONE, recipientAccountFile.getString(E_PHONE));
            editor.putString("type", "recipient");
            editor.putBoolean("loggedIn", true);
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        launchDashboard(context, true);
    }

    private static class getRecipientFileTask extends AsyncTask<String, Void, String> {

        private SharedPreferences session;
        private Context context;
        private getRecipientFileTask(SharedPreferences session, Context context) {
            this.session = session;
            this.context = context.getApplicationContext();
        }

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
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                final JSONObject fileDetails = new JSONObject(response);
                final String encodedContent = fileDetails.getString("content").replace("\n","");
                final String file = new String(Base64.decode(encodedContent, Base64.DEFAULT));
                final JSONObject fileJson = new JSONObject(file);

                loginAsRecipient(session, fileJson, context);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
