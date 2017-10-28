package com.example.gheggie.virs;

import android.content.Context;
import android.content.Intent;
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

class CustomAdapter extends BaseAdapter {
    private ArrayList<Poem> mPoems = new ArrayList<>();
    private Context mContext;
    private String type;

    CustomAdapter(ArrayList<Poem> poems, Context context, String _type) {
        mPoems = poems;
        mContext = context;
        type = _type;
    }

    @Override
    public int getCount() {
        if(mPoems!=null) {
            return mPoems.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mPoems != null &&
                position < mPoems.size() &&
                position > -1) {
            return mPoems.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        int PPL_iD = 0x10203040;
        return PPL_iD + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomViewHolder viewHolder;

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.users_cell,parent,false);

            viewHolder = new CustomViewHolder(convertView);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (CustomViewHolder) convertView.getTag();
        }

        final Poem poem = (Poem) getItem(position);
        if (type.equals("Snapped")) {
            viewHolder.image.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(poem.getPoetView()).into(viewHolder.image);
        } else {
            viewHolder.image.setVisibility(View.GONE);
        }
        viewHolder.title.setTextColor(ContextCompat.getColor(mContext, R.color.blackColor));
        viewHolder.title.setText(poem.getTitle());

        String[] poemDate = poem.getDate().split("-");
        viewHolder.date.setText(poemDate[0]);

        viewHolder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent poemIntent = new Intent(mContext, PoemActivity.class);
                    poemIntent.putExtra(VirsUtils.FEED_POEM, poem);
                    mContext.startActivity(poemIntent);
                }
            });

        viewHolder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent poemIntent = new Intent(mContext, PoemActivity.class);
                    poemIntent.putExtra(VirsUtils.FEED_POEM, poem);
                    mContext.startActivity(poemIntent);
                }
            });

        viewHolder.date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent poemIntent = new Intent(mContext, PoemActivity.class);
                    poemIntent.putExtra(VirsUtils.FEED_POEM, poem);
                    mContext.startActivity(poemIntent);
                }
            });
        return convertView;
    }
}

class CustomViewHolder {
    TextView title;
    TextView date;
    CircleImageView image;

    CustomViewHolder(View v) {
        image = (CircleImageView) v.findViewById(R.id.event_image);
        title = (TextView)v.findViewById(R.id.event_name);
        date = (TextView)v.findViewById(R.id.event_type);
    }
}
