package com.example.gheggie.virs;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by gheggie on 10/18/17.
 */

public class UserPoemAdapter extends BaseAdapter {
    private ArrayList<Poem> mPoems = new ArrayList<>();
    private Context mContext;

    UserPoemAdapter(ArrayList<Poem> poems, Context context) {
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
        UserViewHolder viewHolder;

        if(convertView == null) {

            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.users_cell,parent,false);

            viewHolder = new UserViewHolder(convertView);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (UserViewHolder) convertView.getTag();
        }

        Poem poem = (Poem) getItem(position);
        viewHolder.image.setVisibility(View.GONE);
        viewHolder.title.setTextColor(ContextCompat.getColor(mContext, R.color.blackColor));
        viewHolder.title.setText(poem.getTitle());
        String[] poemDate = poem.getDate().split("-");
        viewHolder.date.setText(poemDate[0]);
        return convertView;
    }
}

class UserViewHolder{
    TextView title;
    TextView date;
    CircleImageView image;

    UserViewHolder(View v) {
        image = (CircleImageView) v.findViewById(R.id.event_image);
        title = (TextView)v.findViewById(R.id.event_name);
        date = (TextView)v.findViewById(R.id.event_type);
    }

}
