package com.example.gheggie.virs;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

class StreamAdapter extends RecyclerView.Adapter<StreamAdapter.SimpleViewHolder> {

    private ArrayList<Stream> mStreams = new ArrayList<>();
    private Context mContext;

    StreamAdapter(ArrayList<Stream> stream, Context context) {
        mStreams = stream;
        mContext = context;
    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder {
        final CircleImageView streamView;

        SimpleViewHolder(View view) {
            super(view);
            streamView = (CircleImageView) view.findViewById(R.id.stream_pic);
        }
    }

    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.stream_cell, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, int position) {
        Picasso.with(mContext).load(mStreams.get(position).getUserIcon()).into(holder.streamView);
        holder.streamView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent(mContext, WatchStreamActivity.class);
                viewIntent.putExtra(VirsUtils.STREAM_URL, mStreams.get(holder.getAdapterPosition()));
                mContext.startActivity(viewIntent);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        int PPL_iD = 0x10203040;
        return PPL_iD + position;
    }

    @Override
    public int getItemCount() {
        if(mStreams !=null) {
            return mStreams.size();
        }
        return 0;
    }
}


