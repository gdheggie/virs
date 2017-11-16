package com.example.gheggie.virs;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

class VenueAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Venue> mVenues = new ArrayList<>();

    VenueAdapter(ArrayList<Venue> venues, Context context) {
        mVenues = venues;
        mContext = context;
    }

    @Override
    public int getCount() {
        if(mVenues!=null) {
            return mVenues.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mVenues != null &&
                position < mVenues.size() &&
                position > -1) {
            return mVenues.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        int PPL_iD = 0x10305070;
        return PPL_iD + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.users_cell,parent,false);
        }

        final Venue venue = (Venue) getItem(position);
        CircleImageView  image = (CircleImageView) convertView.findViewById(R.id.event_image);
        TextView title = (TextView)convertView.findViewById(R.id.event_name);
        TextView name = (TextView)convertView.findViewById(R.id.event_type);

        image.setVisibility(View.VISIBLE);
        Picasso.with(mContext).load(venue.getVenueLogo()).into(image);
        title.setTextColor(ContextCompat.getColor(mContext, R.color.blackColor));
        title.setText(venue.getVenueName());
        name.setMaxWidth(600);
        name.setText(venue.getVenueTitle());

        return convertView;
    }
}
