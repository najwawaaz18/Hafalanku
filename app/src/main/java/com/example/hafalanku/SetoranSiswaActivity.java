package com.example.hafalanku;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SetoranSiswaActivity extends AppCompatActivity {

    Spinner spinnerNama, spinnerSurah, spinnerJuz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setoran_siswa);

//        String[] items = {"", "Item 1", "Item 2", "Item 3"};
//
//        spinner = findViewById(R.id.student_name_spinner);
//
//        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, R.layout.custom_spinner_item, items, false);
//        spinner.setAdapter(adapter);

        List<String> juz_lists = new ArrayList<>();
        juz_lists.add("");
        juz_lists.add("Juz 29");

        spinnerJuz = findViewById(R.id.juz_spinner);

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, R.layout.custom_spinner_item, juz_lists, false);
        spinnerJuz.setAdapter(adapter);

        spinnerNama = findViewById(R.id.student_name_spinner);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://hafalanku-c0546-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        DatabaseReference namaRef = databaseReference.child("Users");

        namaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> studentNames = new ArrayList<>();
                studentNames.add("");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.getKey();
                    String userId = String.valueOf((Long) snapshot.child("userid").getValue());
                    String userType = snapshot.child("usertype").getValue(String.class);
                    if (userType != null && userType.equals("orangtua")) {
                        String concatenated = name + " - " + userId;
                        studentNames.add(concatenated);
                    }
                }

                CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(SetoranSiswaActivity.this, R.layout.custom_spinner_item, studentNames, false);
                spinnerNama.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

        spinnerSurah = findViewById(R.id.surat_spinner);

        DatabaseReference surahRef = databaseReference.child("Surah");

        surahRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> surahNames = new ArrayList<>();
                surahNames.add("");

                for (DataSnapshot surahSnapshot : dataSnapshot.getChildren()) {
                    String namaSurah = surahSnapshot.child("nama_surah").getValue(String.class);

//                    String surahText = namaSurah;
                    surahNames.add(namaSurah);
                }

                CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(SetoranSiswaActivity.this, R.layout.custom_spinner_item, surahNames, false);
                spinnerSurah.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

    }
}
