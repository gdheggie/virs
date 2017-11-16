package com.example.gheggie.virs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

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
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.poem_cell,parent,false);
        }
        sortArray(mPoems);

        ImageView poetView = (ImageView)convertView.findViewById(R.id.poet_image);
        TextView poetName = (TextView)convertView.findViewById(R.id.poet_name);
        TextView poemPreview = (TextView)convertView.findViewById(R.id.poem_preview);
        TextView snapCount = (TextView)convertView.findViewById(R.id.snap_count);
        ImageView chevron = (ImageView)convertView.findViewById(R.id.chevron_feed);
        TextView poetDate = (TextView)convertView.findViewById(R.id.time_ago);

        final Poem poem = mPoems.get(position);
        if(!poem.getPoetView().equals("")) {
            Picasso.with(mContext).load(poem.getPoetView()).into(poetView);
        }
        poetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(mContext, UsersProfileActivity.class);
                userIntent.putExtra(VirsUtils.USER_CLICKED, poem.getPoetId());
                mContext.startActivity(userIntent);
            }
        });
        poetName.setText(poem.getPoet());
        poetName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(mContext, UsersProfileActivity.class);
                userIntent.putExtra(VirsUtils.USER_CLICKED, poem.getPoetId());
                mContext.startActivity(userIntent);
            }
        });
        chevron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent poemIntent = new Intent(mContext, PoemActivity.class);
                poemIntent.putExtra(VirsUtils.FEED_POEM, poem);
                mContext.startActivity(poemIntent);
            }
        });
        if(poem.getPoem().length() > 90) {
            String previewOfPoem = poem.getPoem().substring(0,80);
            poemPreview.setText(previewOfPoem + "...");
        } else {
            poemPreview.setText(poem.getPoem());
        }
        poemPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent poemIntent = new Intent(mContext, PoemActivity.class);
                poemIntent.putExtra(VirsUtils.FEED_POEM, poem);
                mContext.startActivity(poemIntent);
            }
        });
        if (poem.getSnapCount() == 1) {
            String snaps = " " + poem.getSnapCount() + "\n  snap";
            snapCount.setText(String.valueOf(poem.getSnapCount()));
            snapCount.setText(snaps);
        } else {
            String snaps = poem.getSnapCount() + "\nsnaps";
            snapCount.setText(snaps);
        }

        poetDate.setText(howLongAgo(poem.getDate()));

        return convertView;
    }

    private void sortArray(ArrayList<Poem> _mPoems){
        Collections.sort(_mPoems, new Comparator<Poem>() {
            @Override
            public int compare(Poem o1, Poem o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });
    }

    private String howLongAgo(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy-hh:mm:ss", Locale.US);
        Date poemTime = new Date();
        Date nowTime = new Date();
        String agoTime = "huh";
        try {
            poemTime = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long timePast = nowTime.getTime() - poemTime.getTime();

        long seconds = 1000;
        long minutes = seconds * 60;
        long hours = minutes * 60;
        long days = hours * 24;

        long dayCount = timePast / days;
        timePast = timePast % days;

        long hourCount = timePast / hours;
        timePast = hours / timePast;

        long minCount = timePast / minutes;
        timePast = timePast % minutes;

        long secCount = timePast / seconds;

        if (dayCount > 0) {
            if(dayCount <= 6) {
                agoTime = dayCount + "d";
            } else {
                String poemDate = poemTime.toString();
                agoTime = poemDate.substring(0,10);
            }
        } else if (hourCount < 24 && hourCount > 0) {
            agoTime = hourCount + "h";
        } else if (minCount > 0 && minCount < 60) {
            agoTime = minCount + "m";
        } else if (secCount > 0 && secCount < 60) {
            agoTime = secCount + "s";
        }

        return agoTime;
    }
}
