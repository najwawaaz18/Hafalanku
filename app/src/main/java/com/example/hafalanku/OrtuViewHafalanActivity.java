package com.example.hafalanku;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class OrtuViewHafalanActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String username, userid;
    private TextView nameTextView;
    private List<String> namaSurahList;
    private HashMap<String, String> hafalanMap;
    ImageView profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ortu_view_hafalan_siswa);

        profileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrtuViewHafalanActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString("username", "");
        userid = preferences.getString("userid", "");

        String namaText = "Nama Siswa: " + username;
        nameTextView = findViewById(R.id.text_nama_siswa);
        nameTextView.setText(namaText);

        namaSurahList = new ArrayList<>(Arrays.asList(
                "Al-Mulk", "Al-Qalam", "Al-Haqqah", "Al-Ma'arij", "Nuh", "Al-Jin",
                "Al-Muzzammil", "Al-Muddatsir", "Al-Qiyamah", "Al-Insan", "Al-Mursalat"
        ));

        mDatabase = FirebaseDatabase.getInstance("https://hafalanku-c0546-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        DatabaseReference dataRef = mDatabase.child("Hafalan").child(userid).child("status_hafalan");

        hafalanMap = new HashMap<>();

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                TableLayout tableLayout = findViewById(R.id.table_ortu_view);
                if (tableLayout.getChildCount() == 0 || tableLayout.getChildAt(0).getId() != R.id.header_ortu_view_row) {
                    tableLayout.removeAllViews();
                }

//                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
//                    String nama_surah = childSnapshot.getKey();
//                    assert nama_surah != null;
//                    String status = String.valueOf(childSnapshot.getValue());
//
//
//                    addRowToTable(nama_surah, status);
//                }

                hafalanMap.clear();

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String nama_surah = childSnapshot.getKey();
                    assert nama_surah != null;
                    String status = String.valueOf(childSnapshot.getValue());

                    hafalanMap.put(nama_surah, status);
                }

                for (String namaSurah : namaSurahList) {
                    String status = hafalanMap.get(namaSurah);

                    if (status != null) {
                        addRowToTable(namaSurah, status);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Failed to read data", error.toException());
            }
        });
    }

    private void addRowToTable(String nama_surah, String status) {
        LayoutInflater inflater = LayoutInflater.from(this);
        TableRow rowLayout = (TableRow) inflater.inflate(R.layout.table_ortu_view_layout, null);

        ((TextView) rowLayout.findViewById(R.id.style_nama_surah_row)).setText(nama_surah);
        ((TextView) rowLayout.findViewById(R.id.style_status_row)).setText(status);

        TableLayout tableLayout = findViewById(R.id.table_ortu_view);
        tableLayout.addView(rowLayout);
    }

}
