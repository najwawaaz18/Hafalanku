package com.example.hafalanku;

import android.os.Bundle;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MutabaahVideoActivity extends AppCompatActivity {

    private List<VideoItem> videoList;
    private VideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mutabaah_video);

        GridView gridView = findViewById(R.id.gridview);
        videoList = new ArrayList<>();
        adapter = new VideoAdapter(this, videoList);
        gridView.setAdapter(adapter);

        loadVideosFromFirebase();
    }

    private void loadVideosFromFirebase() {
        System.out.println("sini loh");
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
                // Penanganan kesalahan pengambilan data dari Firebase
            }
        });
    }
}
