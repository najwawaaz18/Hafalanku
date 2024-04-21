package com.example.hafalanku;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class VideoAdapter extends ArrayAdapter<VideoItem> {
    private Context context;
    private List<VideoItem> videoList;

    public VideoAdapter(Context context, List<VideoItem> videoList) {
        super(context, 0, videoList);
        this.context = context;
        this.videoList = videoList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grid_item, null);
        }

        ImageView imageView = view.findViewById(R.id.grid_image);
        TextView textView = view.findViewById(R.id.item_name);

        Glide.with(context)
                .load(videoList.get(position).getVideoUrl())
                .into(imageView);

        textView.setText(videoList.get(position).getVideoTitle());

        return view;
    }
}
