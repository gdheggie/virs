package com.example.gheggie.virs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class VenueFragment extends Fragment {

    public static final String TAG = "VenueFragment.TAG";
    private ArrayList<Venue> venues = new ArrayList<>();
    private ListView venueListView;


    public static VenueFragment newInstance(String location) {

        Bundle args = new Bundle();

        VenueFragment fragment = new VenueFragment();
        args.putString(VirsUtils.USER_WITHIN, location);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.venue_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String userLocation = getArguments().getString(VirsUtils.USER_WITHIN);
        ImageButton backToEventSearch = (ImageButton) getActivity().findViewById(R.id.back_event);
        backToEventSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment();
            }
        });
        venueListView = (ListView)getActivity().findViewById(R.id.event_list);
        venueListView.setOnItemClickListener(venueClick);

        if(checkConnection()) {
            EventAsync eventSync = new EventAsync(getActivity(), venues);
            eventSync.execute(userLocation);
        }

        if(venues != null && checkConnection()) {
            populateEventList(venues);
        }
    }

    private void populateEventList(ArrayList<Venue> venueList){
        VenueAdapter venueAdapter = new VenueAdapter(venueList, getActivity());
        venueListView.setAdapter(venueAdapter);
        venueAdapter.notifyDataSetChanged();
    }

    private final ListView.OnItemClickListener venueClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            VenueDetailFragment venueDetailFrag = VenueDetailFragment.newInstance(venues.get(position));
            getFragmentManager().beginTransaction().add(
                    R.id.venue_frame,
                    venueDetailFrag,
                    VenueDetailFragment.TAG
            ).commit();
        }
    };

    // check connection
    private boolean checkConnection() {
        ConnectivityManager mgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mgr != null) {
            NetworkInfo netInfo = mgr.getActiveNetworkInfo();
            if (netInfo != null) {
                if (netInfo.isConnected()) {
                    // if this is true, will run eventAsync
                    return true;
                }
            } else { // if there is no active connection
                Toast.makeText(
                        getActivity(), "Check connection",
                        Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    private void removeFragment(){
        getActivity().getSupportFragmentManager().beginTransaction().
                remove(this).commit();
    }
}
