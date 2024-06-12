package com.example.hafalanku;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class IndividuVideoAdapter extends RecyclerView.Adapter<IndividuVideoAdapter.MyViewHolder> {

    private List<VideoItem> videoItemList;
    private Context context;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public interface OnUploadClickListener {
        void onUploadClick(int position);
    }

    private OnDeleteClickListener onDeleteClickListener;
    private OnUploadClickListener onUploadClickListener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSurat, tvUploadTime;
        public ImageView iconDelete, iconUpload;

        public MyViewHolder(View v) {
            super(v);
            tvSurat = v.findViewById(R.id.tv_surat);
            tvUploadTime = v.findViewById(R.id.tv_upload_time);
            iconDelete = v.findViewById(R.id.icon_delete);
            iconUpload = v.findViewById(R.id.icon_upload);
        }
    }

    public IndividuVideoAdapter(Context context, List<VideoItem> videoItemList) {
        this.context = context;
        this.videoItemList = videoItemList;
    }

    @NonNull
    @Override
    public IndividuVideoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_item_siswa, parent, false);
        return new MyViewHolder(v);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        onDeleteClickListener = listener;
    }

    public void setOnUploadClickListener(OnUploadClickListener listener) {
        onUploadClickListener = listener;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        VideoItem videoItem = videoItemList.get(position);
        holder.tvSurat.setText(videoItem.getVideoTitle());
        holder.tvUploadTime.setText(videoItem.getUploadTime());

        holder.iconDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(holder.getAdapterPosition());
                }
            }
        });

        holder.iconUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onUploadClickListener != null) {
                    onUploadClickListener.onUploadClick(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoItemList.size();
    }

}
