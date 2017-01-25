package kkmp.sharktank;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends AppCompatActivity {

    EditText username_field;
    EditText password_field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        username_field = (EditText) findViewById(R.id.username_field);
        password_field = (EditText) findViewById(R.id.password_field);
    }

    public void clickedButton_login(View view) {
        new getRequest().execute("https://raw.githubusercontent.com/KC-7/CarePear-Data/master/account/list");
    }

    private void accountType(JSONObject list) {
        String username_entry = username_field.getText().toString();

        // If the list of accounts contains the username
        if (list.has(username_entry)) {

            String password_entry = password_field.getText().toString();

            try {
                String pass = list.getString(username_entry);
                if (pass.equals(password_entry)) {
                    toast("Logging in...");
                } else {
                    toast("Wrong password.");
                }
            } catch (JSONException e) {
                toast("ERROR: User Existent, Password Nonexistent.");
                e.printStackTrace();
            }

        } else {
            toast("Wrong username.");
        }
    }

    private class getRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urlString) {
            try {

                final URL url = new URL(urlString[0]);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    return readStream(connection.getInputStream());
                }

            } catch (IOException e) {
                toast("ERROR: Connection failure.");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                final JSONObject list = new JSONObject(response);
                accountType(list);
            } catch (JSONException e) {
                toast("ERROR: Retrieval error.");
                e.printStackTrace();
            }
        }
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
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
        return response.toString();
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

}
