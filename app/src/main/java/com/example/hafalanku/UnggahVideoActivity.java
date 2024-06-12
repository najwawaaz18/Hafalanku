package com.example.hafalanku;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class UnggahVideoActivity extends AppCompatActivity {

    private static final int PICK_VIDEO_CODE = 1;
    private Button uploadButton;
    Spinner spinnerSurah;
    private FirebaseStorage storage;
    private DatabaseReference databaseReference;
    private String username;
    private String userid;
    ImageView profileIcon;
    private RecyclerView videoRecyclerView;
    private List<VideoItem> videoList;
    private IndividuVideoAdapter adapter;
    private TextView nameTextView;
    private List<String> surahNames;
    private List<String> surahIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unggah_video_page);

        SharedPreferences preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString("username", "");
        userid = preferences.getString("userid", "");

        String namaText = "Nama Siswa: " + username;
        nameTextView = findViewById(R.id.text_nama_siswa);
        nameTextView.setText(namaText);

        videoRecyclerView = findViewById(R.id.recycler_view_video_siswa);
        videoList = new ArrayList<>();
        adapter = new IndividuVideoAdapter(this, videoList);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoRecyclerView.setAdapter(adapter);

        adapter.setOnDeleteClickListener(new IndividuVideoAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                showDeleteConfirmationDialog(position);
//                deleteVideo(position);
            }
        });

        spinnerSurah = findViewById(R.id.surat_spinner);
//        DatabaseReference surahRef = FirebaseDatabase.getInstance("https://hafalanku-c0546-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("Surah");
//
//        surahRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<String> surahNames = new ArrayList<>();
//                surahNames.add("");
//
//                for (DataSnapshot surahSnapshot : dataSnapshot.getChildren()) {
//                    String namaSurah = surahSnapshot.child("nama_surah").getValue(String.class);
//
////                    String surahText = namaSurah;
//                    surahNames.add(namaSurah);
//                }
//
//                CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(UnggahVideoActivity.this, R.layout.custom_spinner_item, surahNames, false);
//                spinnerSurah.setAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Handle error
//            }
//        });

        adapter.setOnUploadClickListener(new IndividuVideoAdapter.OnUploadClickListener() {
            @Override
            public void onUploadClick(int position) {
                VideoItem videoItem = videoList.get(position);
                ArrayAdapter<String> spinnerAdapter = (ArrayAdapter<String>) spinnerSurah.getAdapter();
                int positionInSpinner = spinnerAdapter.getPosition(videoItem.getVideoTitle());
                spinnerSurah.setSelection(positionInSpinner);
                deleteVideoToReplace(position);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_VIDEO_CODE);
            }
        });

        profileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UnggahVideoActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        storage = FirebaseStorage.getInstance("gs://hafalanku-c0546.appspot.com");
        databaseReference = FirebaseDatabase.getInstance("https://hafalanku-c0546-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        uploadButton = findViewById(R.id.upload_button);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_VIDEO_CODE);
            }
        });

        loadVideosFromFirebase();
        loadUserHafalanData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_CODE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();

            if (videoUri != null) {
                uploadVideoToFirebase(videoUri);
            } else {
                Toast.makeText(UnggahVideoActivity.this, "No video selected!", Toast.LENGTH_SHORT).show();
            }
        }
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
                Toast.makeText(UnggahVideoActivity.this, "Failed to load videos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadVideoToFirebase(Uri videoUri) {
        // Create a progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Video...");
        progressDialog.show();

        StorageReference videoRef = storage.getReference().child("videos/" + videoUri.getLastPathSegment());

        // Upload
        UploadTask uploadTask = videoRef.putFile(videoUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();

                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        // Download URL
                        String uploadDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        String uploadTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

                        String videoId = generateRandomId();
                        String selectedSurah = spinnerSurah.getSelectedItem().toString();

                        HashMap<String, Object> videoData = new HashMap<>();
                        videoData.put("nama_surah", selectedSurah);
                        videoData.put("upload_date", uploadDate);
                        videoData.put("time_date", uploadTime);
                        videoData.put("video_url", downloadUrl.toString());

                        DatabaseReference userRef = databaseReference.child("Videos");
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(username)) {
                                    userRef.child(username).child(videoId).setValue(videoData);
                                    Toast.makeText(UnggahVideoActivity.this, "Berhasil mengunggah video!", Toast.LENGTH_SHORT).show();
                                } else {
                                    userRef.child(username).setValue(username, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            if (error == null) {
                                                userRef.child(username).child(videoId).setValue(videoData);
                                                Toast.makeText(UnggahVideoActivity.this, "Berhasil mengunggah video!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(UnggahVideoActivity.this, "Gagal mengunggah video.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(UnggahVideoActivity.this, "Gagal mengunggah video.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UnggahVideoActivity.this, "Gagal mengunggah video.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UnggahVideoActivity.this, "Gagal mengunggah video.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String generateRandomId() {
        byte[] randomBytes = new byte[16];
        new SecureRandom().nextBytes(randomBytes);

        StringBuilder sb = new StringBuilder();
        for (byte b : randomBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
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
//                        videoList.remove(position);
//                        adapter.notifyItemRemoved(position);
                        loadVideosFromFirebase();
                        Toast.makeText(UnggahVideoActivity.this, "Video berhasil dihapus.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UnggahVideoActivity.this, "Gagal menghapus video.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(UnggahVideoActivity.this, "Gagal menghapus video.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteVideoToReplace(int position) {
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
                    } else {
                        //
                    }
                });
            } else {
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

    private void loadUserHafalanData() {
        DatabaseReference userHafalanRef = FirebaseDatabase.getInstance("https://hafalanku-c0546-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("Hafalan").child(userid);

        userHafalanRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> surahTidakLulus = new ArrayList<>();
                surahTidakLulus.add("");
                for (DataSnapshot surahSnapshot : dataSnapshot.child("status_hafalan").getChildren()) {
                    String status = surahSnapshot.getValue(String.class);
                    if ("Tidak Lulus".equals(status)) {
                        String surahName = surahSnapshot.getKey();
                        surahTidakLulus.add(surahName);
                        Log.d("SurahTidakLulus", surahName);
                    }
                }

                CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(UnggahVideoActivity.this, R.layout.custom_spinner_item, surahTidakLulus, false);
                spinnerSurah.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
