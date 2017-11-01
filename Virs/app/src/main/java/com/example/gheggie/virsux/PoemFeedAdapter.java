package com.example.gheggie.virsux;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class PoemFeedAdapter extends BaseAdapter{

    private ArrayList<Poem> mPoems = new ArrayList<>();
    private Context mContext;

    PoemFeedAdapter(ArrayList<Poem> poems, Context context) {
        mPoems = poems;
        mContext = context;
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
        if( mPoems!= null &&
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
        ViewHolder viewHolder;

        if(convertView == null) {

            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.poem_cell,parent,false);

            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final Poem poem = mPoems.get(position);
        if(!poem.getPoetView().equals("")) {
            Picasso.with(mContext).load(poem.getPoetView()).into(viewHolder.poetView);
        }
        viewHolder.poetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(mContext, UsersProfileActivity.class);
                userIntent.putExtra(VirsUtils.USER_CLICKED, poem.getPoetId());
                mContext.startActivity(userIntent);
            }
        });
        viewHolder.poetName.setText(poem.getPoet());
        viewHolder.poetName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(mContext, UsersProfileActivity.class);
                userIntent.putExtra(VirsUtils.USER_CLICKED, poem.getPoetId());
                mContext.startActivity(userIntent);
            }
        });
        viewHolder.chevron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent poemIntent = new Intent(mContext, PoemActivity.class);
                poemIntent.putExtra(VirsUtils.FEED_POEM, poem);
                mContext.startActivity(poemIntent);
            }
        });
        if(poem.getPoem().length() > 90) {
            String previewOfPoem = poem.getPoem().substring(0,80);
            viewHolder.poemPreview.setText(previewOfPoem + "...");
        } else {
            viewHolder.poemPreview.setText(poem.getPoem());
        }
        viewHolder.poemPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent poemIntent = new Intent(mContext, PoemActivity.class);
                poemIntent.putExtra(VirsUtils.FEED_POEM, poem);
                mContext.startActivity(poemIntent);
            }
        });
        if (poem.getSnapCount() == 1) {
            String snaps = " " + poem.getSnapCount() + "\n  snap";
            viewHolder.snapCount.setText(String.valueOf(poem.getSnapCount()));
            viewHolder.snapCount.setText(snaps);
        } else {
            String snaps = poem.getSnapCount() + "\nsnaps";
            viewHolder.snapCount.setText(snaps);
        }

        return convertView;
    }

}

class ViewHolder{
    ImageView poetView;
    TextView poetName;
    TextView poemPreview;
    TextView snapCount;
    ImageView chevron;

    ViewHolder(View v) {
        poetView = (ImageView)v.findViewById(R.id.poet_image);
        poetName = (TextView)v.findViewById(R.id.poet_name);
        poemPreview = (TextView)v.findViewById(R.id.poem_preview);
        snapCount = (TextView)v.findViewById(R.id.snap_count);
        chevron = (ImageView)v.findViewById(R.id.chevron_feed);
    }

}
