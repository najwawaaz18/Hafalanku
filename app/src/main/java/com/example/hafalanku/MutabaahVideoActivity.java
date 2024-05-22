package com.example.hafalanku;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MutabaahVideoActivity extends AppCompatActivity {

    private List<VideoItem> videoList;
    private VideoAdapter adapter;
    ImageView profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mutabaah_video);

        GridView gridView = findViewById(R.id.gridview);
        videoList = new ArrayList<>();
        adapter = new VideoAdapter(this, videoList);
        gridView.setAdapter(adapter);

        profileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MutabaahVideoActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoItem videoItem = videoList.get(position);

                Intent intent = new Intent(MutabaahVideoActivity.this, VideoPlaybackActivity.class);
                intent.putExtra("video_url", videoItem.getVideoUrl());
                startActivity(intent);
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteConfirmationDialog(position);
                return true;
            }
        });

        loadVideosFromFirebase();
    }

    private void loadVideosFromFirebase() {
//        System.out.println("sini loh");
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://hafalanku-c0546-default-rtdb.asia-southeast1.firebasedatabase.app");
        database.getReference("Videos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot videoSnapshot : userSnapshot.getChildren()) {
                        String videoUrl = videoSnapshot.child("video_url").getValue(String.class);
                        String videoTitle = userSnapshot.getKey();
                        VideoItem videoItem = new VideoItem(videoUrl, videoTitle);
                        videoList.add(videoItem);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }
        });
    }

    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Video")
                .setMessage("Are you sure you want to delete this video?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteVideo(position);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteVideo(int position) {
        VideoItem videoItem = videoList.get(position);

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(videoItem.getVideoUrl());

        storageRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DatabaseReference videoRef = FirebaseDatabase.getInstance("https://hafalanku-c0546-default-rtdb.asia-southeast1.firebasedatabase.app")
                        .getReference("Videos")
                        .child(videoItem.getVideoTitle());

                videoRef.removeValue().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        videoList.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MutabaahVideoActivity.this, "Video berhasil dihapus.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MutabaahVideoActivity.this, "Gagal menghapus video.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(MutabaahVideoActivity.this, "Gagal menghapus video.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
