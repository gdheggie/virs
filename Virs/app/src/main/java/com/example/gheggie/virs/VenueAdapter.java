package com.example.gheggie.virs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
        VenueViewHolder viewHolder;

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.users_cell,parent,false);

            viewHolder = new VenueViewHolder(convertView);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (VenueViewHolder) convertView.getTag();
        }

        final Venue venue = (Venue) getItem(position);
        viewHolder.image.setVisibility(View.VISIBLE);
        Picasso.with(mContext).load(venue.getVenueLogo()).into(viewHolder.image);
        viewHolder.title.setTextColor(ContextCompat.getColor(mContext, R.color.blackColor));
        viewHolder.title.setText(venue.getVenueName());
        viewHolder.name.setMaxWidth(600);
        viewHolder.name.setText(venue.getVenueTitle());

        return convertView;
    }
}

class VenueViewHolder {
    TextView title;
    TextView name;
    CircleImageView image;

    VenueViewHolder(View v) {
        image = (CircleImageView) v.findViewById(R.id.event_image);
        title = (TextView)v.findViewById(R.id.event_name);
        name = (TextView)v.findViewById(R.id.event_type);
    }

}