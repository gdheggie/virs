package com.example.gheggie.virs;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class EventFragment extends Fragment implements LocationListener {

    private String userLatitude;
    private String userLongitude;
    private Location mLocation;
    private String userWithin;
    private EditText milesText;
    private boolean mRequestingUpdates = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        milesText = (EditText) getActivity().findViewById(R.id.miles_edit_text_field);

        // Get our location manager.
        LocationManager mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if(ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && !mRequestingUpdates) {

            // Request location updates using 'this' as our LocationListener.
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    2000, 10.0f, this);
            mRequestingUpdates = false;

            // Get our last known location and check if it's a valid location.
            mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (mLocation != null) {
            userLatitude = String.valueOf(mLocation.getLatitude());
            userLongitude = String.valueOf(mLocation.getLongitude());
        }

        Button searchEvent = (Button)getActivity().findViewById(R.id.search_event_button);
        searchEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userWithin = milesText.getText().toString();

                if (TextUtils.isEmpty(userWithin)) {
                    milesText.setError("Enter Mile Radius");
                } else {
                    String eventAPI =
                            "https://www.eventbriteapi.com/v3/events/search/?q=Poetry&sort_by=date&location.within="
                                    + userWithin + "mi&location.latitude=" + userLatitude + "&location.longitude="
                                    + userLongitude + "&token=2AVFM5JWNWJFXI37DY6V&expand=venue";
                    VenueFragment venue = VenueFragment.newInstance(eventAPI);
                    getActivity().getSupportFragmentManager().beginTransaction().add(
                            R.id.event_frame,
                            venue,
                            VenueFragment.TAG)
                            .commit();
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
