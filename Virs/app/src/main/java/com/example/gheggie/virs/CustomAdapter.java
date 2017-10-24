package com.example.gheggie.virs;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomAdapter extends BaseAdapter {
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
        CustomViewHolder viewHolder;

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.users_cell,parent,false);

            viewHolder = new CustomViewHolder(convertView);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (CustomViewHolder) convertView.getTag();
        }

        Poem poem = (Poem) getItem(position);
        if(type.equals("Snapped") || type.equals("Events")) {
            viewHolder.image.setVisibility(View.VISIBLE);
            if(type.equals("Snapped")) {
                viewHolder.image.setImageResource(R.color.feedBackground);
            }
        } else {
            viewHolder.image.setVisibility(View.GONE);
        }
        viewHolder.title.setTextColor(ContextCompat.getColor(mContext, R.color.blackColor));
        viewHolder.title.setText(poem.getTitle());

        String[] poemDate = poem.getDate().split("-");
        viewHolder.date.setText(poemDate[0]);


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
