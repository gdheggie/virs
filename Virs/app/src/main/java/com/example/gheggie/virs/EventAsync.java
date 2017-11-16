package com.example.gheggie.virs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

class EventAsync extends AsyncTask<String, String, ArrayList<Venue>> {

    private ArrayList<Venue> venueList = new ArrayList<>();
    private Context mContext;
    private ProgressDialog progress;

    EventAsync(Context context , ArrayList<Venue> venues){
        mContext = context;
        venueList = venues;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progress = new ProgressDialog(mContext);
        progress.setMessage("Loading Events...");
        progress.show();
    }

    @Override
    protected ArrayList<Venue> doInBackground(String... params) {
        String event = networkData(params[0]);

        try {
            JSONObject venues = new JSONObject(event);
            JSONArray events = venues.getJSONArray("events");

            for(int i = 0; i < events.length(); i++) {
                JSONObject obj = events.getJSONObject(i);
                JSONObject name = obj.getJSONObject("name");
                String title = name.getString("text");
                JSONObject start = obj.getJSONObject("start");
                String time = start.getString("local");
                JSONObject logo = obj.getJSONObject("logo");
                JSONObject original = logo.getJSONObject("original");
                String photoUrl = original.getString("url");
                JSONObject venue = obj.getJSONObject("venue");
                JSONObject address = venue.getJSONObject("address");
                String venueAddress = address.getString("localized_address_display");
                String venueName = venue.getString("name");

                String[] strings = new String[]{venueName, title, venueAddress, time, photoUrl};
                publishProgress(strings);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        venueList.add(new Venue(values[0], values[1], values[2], values[3], values[4]));
    }

    @Override
    protected void onPostExecute(ArrayList<Venue> venues) {
        super.onPostExecute(venues);
        progress.dismiss();
    }

    // getting JSON URL
    private String networkData(String urlString) {
        try {
            // create URL Obj
            URL url = new URL(urlString);

            //open connection
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.connect();

            InputStream iS = connection.getInputStream();
            String data = IOUtils.toString(iS);

            iS.close();
            connection.disconnect();

            return data;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
