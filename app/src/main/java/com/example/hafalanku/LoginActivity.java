package com.example.hafalanku;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText usernameText, useridText;
    TextView loginSebagai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        usernameText = findViewById(R.id.edit_text);
        useridText = findViewById(R.id.edit_nisn);

        loginSebagai = findViewById(R.id.textLoginSebagai);
        Intent intent = getIntent();
        String selectedUserType = intent.getStringExtra("USER_TYPE");
        loginSebagai.setText("Login Sebagai " + selectedUserType);


        FirebaseDatabase database = FirebaseDatabase.getInstance("https://hafalanku-c0546-default-rtdb.asia-southeast1.firebasedatabase.app");

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameText.getText().toString();
                String userid = useridText.getText().toString();

                if (username.isEmpty() || userid.isEmpty()) {
                    Log.d("LoginActivity", "Username or userid cannot be empty!");
                    return;
                }

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//                if (currentUser != null) {
                    DatabaseReference userRef = database.getReference().child("Users").child(username);

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Log.d("LoginActivity", "User data found!");
                            Long retrievedUserId = (Long) dataSnapshot.child("userid").getValue();
                            String userType = dataSnapshot.child("usertype").getValue(String.class);
                            System.out.println(dataSnapshot.hasChild("userid"));  // Check if "userid" node exists

                            System.out.println(retrievedUserId);
                            System.out.println(userid);
                            if (retrievedUserId != null && retrievedUserId == Long.parseLong(userid)) {
                                    if ("guru".equalsIgnoreCase(userType)) {
                                        SharedPreferences preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("userid", userid);
                                        editor.putString("username", username);
                                        editor.putString("usertype", userType);
                                        editor.apply();
                                        Toast toast = Toast.makeText(LoginActivity.this, "Berhasil login!", Toast.LENGTH_SHORT);
                                        toast.show();
                                        Intent intent = new Intent(LoginActivity.this, HomeScreenGuruActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        SharedPreferences preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("userid", userid);
                                        editor.putString("username", username);
                                        editor.putString("usertype", userType);
                                        editor.apply();
                                        Toast toast = Toast.makeText(LoginActivity.this, "Berhasil login!", Toast.LENGTH_SHORT);
                                        toast.show();
                                        Intent intent = new Intent(LoginActivity.this, HomeScreenOrangtuaActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
//                                    Log.d("LoginActivity", "Wrong username.");
                                    Toast toast = Toast.makeText(LoginActivity.this, "Password yang Anda masukkan salah.", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            } else {
//                                Log.d("LoginActivity", "Wrong username or password.");
                                Toast toast = Toast.makeText(LoginActivity.this, "Username atau password salah.", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("LoginActivity", "Error reading user data", databaseError.toException());
                        }
                    });
//                } else {
//                    Log.d("LoginActivity", "Please sign in to your account!");
//                }
            }
        });
    }
}
