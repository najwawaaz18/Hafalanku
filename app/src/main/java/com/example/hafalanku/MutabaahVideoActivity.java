package com.example.hafalanku;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    ImageView profileIcon;
    private String username;
    private RecyclerView videoRecyclerView;
    private List<VideoItem> videoList;
    private GuruIndividuVideoAdapter adapter;
    private TextView nameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mutabaah_individu);

        videoRecyclerView = findViewById(R.id.recycler_view_video_guru);
        videoList = new ArrayList<>();
        adapter = new GuruIndividuVideoAdapter(this, videoList);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoRecyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        String namaText = "Nama Siswa: " + username;
        nameTextView = findViewById(R.id.text_nama_siswa);
        nameTextView.setText(namaText);

        adapter.setOnDeleteClickListener(new GuruIndividuVideoAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                showDeleteConfirmationDialog(position);
//                deleteVideo(position);
            }
        });

        adapter.setOnPlayClickListener(new GuruIndividuVideoAdapter.OnPlayClickListener() {
            @Override
            public void onPlayClick(int position) {
                VideoItem videoItem = videoList.get(position);
                Intent intent = new Intent(MutabaahVideoActivity.this, VideoPlaybackActivity.class);
                intent.putExtra("video_url", videoItem.getVideoUrl());
                startActivity(intent);
            }
        });

        profileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MutabaahVideoActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                VideoItem videoItem = videoList.get(position);
//
//                Intent intent = new Intent(MutabaahVideoActivity.this, VideoPlaybackActivity.class);
//                intent.putExtra("video_url", videoItem.getVideoUrl());
//                startActivity(intent);
//            }
//        });

//        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                showDeleteConfirmationDialog(position);
//                return true;
//            }
//        });

        loadVideosFromFirebase();
    }

    private void loadVideosFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://hafalanku-c0546-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference videosRef = database.getReference().child("Videos").child(username);

        videosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                videoList.clear();
                for (DataSnapshot videoSnapshot : dataSnapshot.getChildren()) {
                    String videoUrl = videoSnapshot.child("video_url").getValue(String.class);
                    String videoTitle = "Surat " + videoSnapshot.child("nama_surah").getValue(String.class);
                    String videoUploadTime = videoSnapshot.child("upload_date").getValue(String.class) + ", " + videoSnapshot.child("time_date").getValue(String.class);
                    VideoItem videoItem = new VideoItem(videoUrl, videoTitle);
                    videoItem.setVideoId(videoSnapshot.getKey());
                    videoItem.setUploadTime(videoUploadTime);
                    videoItem.setVideoUserName(username);
                    videoList.add(videoItem);
                }
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MutabaahVideoActivity.this, "Failed to load videos", Toast.LENGTH_SHORT).show();
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
                        .child(videoItem.getVideoUserName())
                        .child(videoItem.getVideoId());

                videoRef.removeValue().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        loadVideosFromFirebase();
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
