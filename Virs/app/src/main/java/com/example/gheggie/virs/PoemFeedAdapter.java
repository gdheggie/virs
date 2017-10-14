package com.example.gheggie.virs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class PoemFeedAdapter extends BaseAdapter {

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
        if( mPoems!=null &&
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

        Poem poem = (Poem) getItem(position);

        //viewHolder.poetView.setImageBitmap(poem);
        viewHolder.poetName.setText(poem.getPoet());
        viewHolder.poemPreview.setText(poem.getPoem());
        viewHolder.snapCount.setText(poem.getSnapCount());

        return convertView;
    }
}

class ViewHolder{
    ImageView poetView;
    TextView poetName;
    TextView poemPreview;
    ImageView snap;
    TextView snapCount;

    ViewHolder(View v) {
        poetView = (ImageView)v.findViewById(R.id.poet_image);
        poetName = (TextView)v.findViewById(R.id.poet_name);
        poemPreview = (TextView)v.findViewById(R.id.poem_preview);
        snap = (ImageView)v.findViewById(R.id.snap_icon);
        snapCount = (TextView)v.findViewById(R.id.snap_count);
    }

}
