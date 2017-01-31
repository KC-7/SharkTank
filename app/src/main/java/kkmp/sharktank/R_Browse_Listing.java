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
 * Created by kchugh on 1/23/2017 at 10:28 AM
 */

public class R_Browse_Listing extends AppCompatActivity {

    private final static String API = "https://api.github.com/repos/KC-7/CarePear-Data/contents/";
    private final ArrayList<HashMap<String, String>> listings = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_browse_listing);

        new getListOfListingsTask().execute(API + "listing/list");
    }

    private void displayListView() {
        ListAdapter listAdapter = new ListViewAdapter(this, listings);
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        HashMap<String, String> requestMap = (HashMap<String, String>)parent.getItemAtPosition(position);
                        final Intent intent = new Intent();
                        intent.putExtra("listingMap", requestMap);
                        intent.setClassName("kkmp.sharktank", "kkmp.sharktank.R_Select_Listing");
                        startActivity(intent);
                    }
                }
        );

    }

    private void processListingFile(JSONObject listingFile) {
        try {

            HashMap<String, String> listingMap = new HashMap<>();
            listingMap.put("title", listingFile.getString("title"));
            listingMap.put("tags", listingFile.getString("tags"));
            listingMap.put("timings", listingFile.getString("timings"));
            listingMap.put("comments", listingFile.getString("comments"));
            listingMap.put("username", listingFile.getString("username"));

            listings.add(listingMap);

        } catch (JSONException e) {
            e.printStackTrace();
            toastL("ERROR: Retrieving listing file details error.");
        }
        displayListView();
    }

    private class getListingFileTask extends AsyncTask<String, Void, String> {
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
                JSONObject listingFile = new JSONObject(list);
                processListingFile(listingFile);
            } catch (JSONException e) {
                toastL("ERROR: Retrieval parsing error.");
                e.printStackTrace();
            }
        }
    }

    private void processListArray(JSONArray listArray) {
        for (int x = listArray.length() - 1; x >= 0; x--) {
            try {
                String listingID = listArray.getString(x);
                new getListingFileTask().execute(API + "listing/listings/" + listingID);
            } catch (JSONException e) {
                e.printStackTrace();
                toastL("ERROR: ID retrieval error.");
            }
        }
    }

    private class getListOfListingsTask extends AsyncTask<String, Void, String> {
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
