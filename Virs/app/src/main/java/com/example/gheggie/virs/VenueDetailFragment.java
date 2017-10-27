package com.example.gheggie.virs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VenueDetailFragment extends Fragment {

    public static final String TAG = "VenueDetailFragment.TAG";
    private Venue venue;
    ImageView venueImage;

    public static VenueDetailFragment newInstance(Venue venue) {

        Bundle args = new Bundle();

        VenueDetailFragment fragment = new VenueDetailFragment();
        args.putSerializable(VirsUtils.VENUE_CLICK, venue);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.venue_detail_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        venue = (Venue)getArguments().getSerializable(VirsUtils.VENUE_CLICK);
        venueImage = (ImageView)getActivity().findViewById(R.id.venue_image);
        TextView venueName = (TextView)getActivity().findViewById(R.id.venue_name);
        TextView venueTitle = (TextView)getActivity().findViewById(R.id.venue_title);
        TextView venueTime = (TextView)getActivity().findViewById(R.id.venue_time);
        TextView venueWhere = (TextView)getActivity().findViewById(R.id.venue_address);

        // Setup Correct Fields
        if(venue != null) {
            venueName.setText(venue.getVenueName());
            venueTitle.setText("Event : " + venue.getVenueTitle());
            //Format Venue Start Time
            String[] venueStartTime = venue.getVenueTime().split("T");
            String clearTime = venueStartTime[1].substring(0,5);
            String venueTrueTime = venueStartTime[0] + " @ " + clearTime;
            venueTime.setText("Time : " + venueTrueTime);
            venueWhere.setText(venue.getVenueLocation());
            VenueLogoAsync venueAsync = new VenueLogoAsync(venueImage);
            venueAsync.execute(venue.getVenueLogo());
        }

        ImageButton back = (ImageButton)getActivity().findViewById(R.id.back_detail);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment();
            }
        });

        Button directions = (Button)getActivity().findViewById(R.id.directions_button);
        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String directionString = "http://maps.google.co.in/maps?q=" + venue.getVenueLocation();
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(directionString));
                startActivity(mapIntent);
            }
        });
    }

    private void removeFragment(){
        getActivity().getSupportFragmentManager().beginTransaction().
                remove(this).commit();
    }

}

class VenueLogoAsync extends AsyncTask<String, Void, Bitmap> {

    private final ImageView imgView;

    VenueLogoAsync(ImageView _imgView) {
        imgView = _imgView;
    }

    // grab logo
    @Override
    protected Bitmap doInBackground(String... params) {
        String bookIcon = params[0];
        return logoImages(bookIcon);
    }

    // set logos
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        imgView.setImageBitmap(bitmap);
    }

    // getting Images
    private Bitmap logoImages(String imageURL) {
        try {
            // create URL obj
            URL urlConnection = new URL(imageURL);

            HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
            connection.connect();

            InputStream iS = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(iS);

            iS.close();
            connection.disconnect();

            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
