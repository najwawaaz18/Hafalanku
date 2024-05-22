package com.example.hafalanku;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Date;
import java.util.HashMap;

public class UnggahVideoActivity extends AppCompatActivity {

    private static final int PICK_VIDEO_CODE = 1;
    private Button uploadButton;

    private FirebaseStorage storage;
    private DatabaseReference databaseReference;
    private String username;
    ImageView profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unggah_video_page);

        profileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UnggahVideoActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString("username", "");

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

                        HashMap<String, Object> videoData = new HashMap<>();
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


}
