package com.example.hafalanku;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    EditText namaField, nisnField;
    Button editButton, logoutButton;

    String currentUsername, currentUserId, currentUserType, currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        namaField = findViewById(R.id.edit_nama);
        nisnField = findViewById(R.id.edit_nisn);
        editButton = findViewById(R.id.edit_button);
        logoutButton = findViewById(R.id.logout_button);

        namaField.setEnabled(false);
        nisnField.setEnabled(false);

        SharedPreferences preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        currentUsername = preferences.getString("username", "");
        currentUserId = preferences.getString("userid", "");
        currentUserType = preferences.getString("usertype", "");
        currentState = "Viewing";

        namaField.setText(currentUsername);
        nisnField.setText(currentUserId);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.remove("userid");
                editor.remove("username");
                editor.apply();

                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentState.equals("Viewing")) {
                    editButton.setText(R.string.update);
                    namaField.setEnabled(true);
                    nisnField.setEnabled(true);
                    currentState = "Editing";
                } else {
                    editButton.setText(R.string.edit);
                    namaField.setEnabled(false);
                    nisnField.setEnabled(false);
                    currentState = "Viewing";

                    String updatedUsername = namaField.getText().toString();
                    String updatedUserid = nisnField.getText().toString();

                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://hafalanku-c0546-default-rtdb.asia-southeast1.firebasedatabase.app");
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    DatabaseReference userRef = database.getReference().child("Users").child(currentUsername);

                    // Create new entry with updated username and userid
                    DatabaseReference newUserRef = database.getReference().child("Users").child(updatedUsername);
                    newUserRef.child("usertype").setValue(currentUserType);
                    newUserRef.child("userid").setValue(Long.parseLong(updatedUserid))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("ProfileActivity", "User data updated successfully!");

                                    userRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("ProfileActivity", "Old user data removed successfully!");

                                            editor.putString("userid", updatedUserid);
                                            editor.putString("username", updatedUsername);
                                            editor.apply();

                                            currentUserId = updatedUserid;
                                            currentUsername = updatedUsername;

                                            Toast.makeText(ProfileActivity.this, "Berhasil memperbarui data pengguna!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("ProfileActivity", "Error removing old user data:", e);
                                            Toast.makeText(ProfileActivity.this, "Gagal memperbarui data pengguna!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("ProfileActivity", "Error updating user data:", e);
                                    Toast.makeText(ProfileActivity.this, "Gagal memperbarui data pengguna!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
}