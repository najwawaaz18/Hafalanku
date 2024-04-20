package com.example.hafalanku;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    Button buttonLoginGuru, buttonLoginOrangtua;
    TextView textViewAbout;

    public static String USER_TYPE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonLoginGuru = findViewById(R.id.buttonLoginGuruWaliKelas);
        buttonLoginOrangtua = findViewById(R.id.buttonLoginOrangtuaSiswa);

        textViewAbout = findViewById(R.id.menuAbout);

        FirebaseDatabase database;
//        database =  FirebaseDatabase.getInstance("https://console.firebase.google.com/u/1/project/hafalanku-c0546/database/hafalanku-c0546-default-rtdb/data/~2F");
//
//        if (database == null) {
//            Log.d("FirebaseInit", "Failed to initialize Firebase Database");
//        } else {
//            database.setPersistenceEnabled(true);
//            Log.d("FirebaseInit", "Firebase Database initialized successfully");
//        }
        buttonLoginGuru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra(USER_TYPE, "Guru");
                startActivity(intent);
            }
        });

        buttonLoginOrangtua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra(USER_TYPE, "Orangtua");
                startActivity(intent);
            }
        });

        textViewAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

    }
}
