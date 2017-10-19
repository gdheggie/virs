package com.example.gheggie.virs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

class PoemFeedAdapter extends BaseAdapter implements View.OnClickListener{

    private ArrayList<Poem> mPoems = new ArrayList<>();
    private Context mContext;
    private Poem poem;

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

        poem = (Poem) getItem(position);
        //viewHolder.poetView.setImageBitmap(poem);
        viewHolder.poetName.setText(poem.getPoet());
        viewHolder.poetName.setOnClickListener(this);
        viewHolder.chevron.setOnClickListener(this);
        String poem_preview = poem.getPoem().substring(0,90);
        viewHolder.poemPreview.setText(poem_preview);
        viewHolder.poemPreview.setOnClickListener(this);
        viewHolder.snapCount.setText(String.valueOf(poem.getSnapCount()));
        return convertView;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.poet_name || v.getId() == R.id.poet_image) {
            Intent userIntent = new Intent(mContext, UsersProfileActivity.class);
            userIntent.putExtra(VirsUtils.USER_CLICKED, poem.getPoetId());
            mContext.startActivity(userIntent);
        } else if (v.getId() == R.id.poem_preview || v.getId() == R.id.chevron_feed) {
            Intent poemIntent = new Intent(mContext, PoemActivity.class);
            poemIntent.putExtra(VirsUtils.FEED_POEM, poem);
            mContext.startActivity(poemIntent);
        }
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
