package kkmp.sharktank;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kchugh on 1/26/2017 at 11:50 PM
 */

public class C_Browse_Request extends AppCompatActivity {

    private final static String API = "https://api.github.com/repos/KC-7/CarePear-Data/contents/";
    private final ArrayList<HashMap<String, String>> requests = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_browse_request);

        new getListOfRequestsTask().execute(API + "request/list");
    }

    private void displayListView() {
        ListAdapter listAdapter = new ListViewAdapter(this, requests);
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        HashMap<String, String> requestMap = (HashMap<String, String>)parent.getItemAtPosition(position);
                        final Intent intent = new Intent();
                        intent.putExtra("requestMap", requestMap);
                        intent.setClassName("kkmp.sharktank", "kkmp.sharktank.C_Select_Request");
                        startActivity(intent);
                    }
                }
        );

    }

    private void processRequestFile(JSONObject requestFile) {
        try {

            HashMap<String, String> requestMap = new HashMap<>();
            requestMap.put("title", requestFile.getString("title"));
            requestMap.put("tags", requestFile.getString("tags"));
            requestMap.put("timings", requestFile.getString("timings"));
            requestMap.put("comments", requestFile.getString("comments"));
            requestMap.put("username", requestFile.getString("username"));
            requestMap.put("code", requestFile.getString("code"));

            requests.add(requestMap);

        } catch (JSONException e) {
            e.printStackTrace();
            toastL("ERROR: Retrieving request file details error.");
        }
        displayListView();
    }

    private class getRequestFileTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urlString) {
            try {
                final URL url = new URL(urlString[0]);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                String token = "c2341499852a34c450" + "fab7a962b8efda429c1522" + ":x-oauth-basic";
                String authString = "Basic " + Base64.encodeToString(token.getBytes(), Base64.DEFAULT);
                connection.setRequestProperty("Authorization", authString);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return Core.readStream(connection.getInputStream());
                } else {
                    throw new AssertionError();
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
                String encodedContent = listDetails.getString("content").replace("\n", "");
                String list = new String(Base64.decode(encodedContent, Base64.DEFAULT));
                JSONObject requestFile = new JSONObject(list);
                processRequestFile(requestFile);
            } catch (JSONException e) {
                toastL("ERROR: Retrieval parsing error.");
                e.printStackTrace();
            }
        }
    }

    private void processListArray(JSONArray listArray) {
        for (int x = listArray.length() - 1; x >= 0; x--) {
            try {
                String requestID = listArray.getString(x);
                new getRequestFileTask().execute(API + "request/requests/" + requestID);
            } catch (JSONException e) {
                e.printStackTrace();
                toastL("ERROR: ID retrieval error.");
            }
        }
    }

    private class getListOfRequestsTask extends AsyncTask<String, Void, String> {
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
                    throw new AssertionError();
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
                JSONArray listArray = new JSONArray(list);
                processListArray(listArray);
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
