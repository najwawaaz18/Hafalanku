package com.example.hafalanku;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
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

    private static final int PICK_VIDEO_CODE = 1; // Request code for picking video
    private Button uploadButton;

    private FirebaseStorage storage;
    private DatabaseReference databaseReference;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unggah_video_page);

        SharedPreferences preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString("username", "");

        // Initialize Firebase Storage and Database references
        storage = FirebaseStorage.getInstance("gs://hafalanku-c0546.appspot.com");
        databaseReference = FirebaseDatabase.getInstance("https://hafalanku-c0546-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        uploadButton = findViewById(R.id.upload_button);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open gallery intent to pick a video
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_VIDEO_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_CODE && resultCode == RESULT_OK) {
            // Get video URI from the intent
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

        // Get a reference to the storage location for the video
        StorageReference videoRef = storage.getReference().child("videos/" + videoUri.getLastPathSegment());

        // Upload the video to Firebase Storage
        UploadTask uploadTask = videoRef.putFile(videoUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();

                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        // Now you can safely access the download URL and perform further operations
                        String uploadDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        String uploadTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

                        // Generate a more secure random video ID (consider using UUID libraries)
                        String videoId = generateRandomId();

                        // Create video data structure
                        HashMap<String, Object> videoData = new HashMap<>();
                        videoData.put("upload_date", uploadDate);
                        videoData.put("time_date", uploadTime);
                        videoData.put("video_url", downloadUrl.toString());

                        // Check if "username" child exists under "Videos"
                        DatabaseReference userRef = databaseReference.child("Videos");
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(username)) {
                                    // Username child exists, save video data under it
                                    userRef.child(username).child(videoId).setValue(videoData);
                                    Toast.makeText(UnggahVideoActivity.this, "Video uploaded successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Username child doesn't exist, create it and then save video data
                                    userRef.child(username).setValue(username, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            if (error == null) {
                                                // Username child created successfully, now save video data
                                                userRef.child(username).child(videoId).setValue(videoData);
                                                Toast.makeText(UnggahVideoActivity.this, "Video uploaded successfully!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Handle error creating username child
                                                Toast.makeText(UnggahVideoActivity.this, "Error creating user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle error reading data
                                Toast.makeText(UnggahVideoActivity.this, "Error uploading video: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure to retrieve download URL
                        Toast.makeText(UnggahVideoActivity.this, "Failed to retrieve download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UnggahVideoActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String generateRandomId() {
        // Menghasilkan array byte acak
        byte[] randomBytes = new byte[16];
        new SecureRandom().nextBytes(randomBytes);

        // Konversi array byte menjadi string heksadesimal
        StringBuilder sb = new StringBuilder();
        for (byte b : randomBytes) {
            sb.append(String.format("%02x", b));
        }

        // Kembalikan ID dalam format string
        return sb.toString();
    }


}
