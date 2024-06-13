package com.example.hafalanku;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SetoranSiswaActivity extends AppCompatActivity {

    Spinner spinnerNama, spinnerSurah;
    Button buttonSelesai;
    String selectedStudentName = "";
    String selectedSurah = "";
    String selectedStudentId = "";
    ImageView profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setoran_siswa);

        profileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetoranSiswaActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

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

        Button buttonSelesai = findViewById(R.id.selesai_button);
        buttonSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSelectedValues();
            }
        });
    }

    private void getSelectedValues() {
        if (spinnerNama.getSelectedItem() != null) {
            String selectedStudent = spinnerNama.getSelectedItem().toString();
            String[] studentInfo = selectedStudent.split(" - ");
            if (studentInfo.length == 2) {
                String studentName = studentInfo[0];
                String studentId = studentInfo[1];
                selectedStudentName = studentName;
                selectedStudentId = studentId;
            }
        }

        if (spinnerSurah.getSelectedItem() != null) {
            selectedSurah = spinnerSurah.getSelectedItem().toString();
        }

        checkAndAddHafalan();
    }


    private void checkAndAddHafalan() {
        if (selectedStudentId.isEmpty() || selectedSurah.isEmpty()) {
            Toast.makeText(SetoranSiswaActivity.this, "Pilih nama siswa dan surah terlebih dahulu.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference hafalanRef = FirebaseDatabase.getInstance("https://hafalanku-c0546-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("Hafalan").child(selectedStudentId);

        hafalanRef.child("status_hafalan").child(selectedSurah).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(SetoranSiswaActivity.this, "Data hafalan sudah ada.", Toast.LENGTH_SHORT).show();
                } else {
                    addHafalan(hafalanRef);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void addHafalan(DatabaseReference hafalanRef) {
        String statusHafalan = "Tidak Lulus";

        hafalanRef.child("nama").setValue(selectedStudentName);
        hafalanRef.child("status_hafalan").child(selectedSurah).setValue(statusHafalan);

        Toast.makeText(SetoranSiswaActivity.this, "Berhasil menambahkan data setoran hafalan!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SetoranSiswaActivity.this, HomeScreenGuruActivity.class);
        startActivity(intent);

        Log.d("SelectedValues", "Selected Student Name: " + selectedStudentName);
        Log.d("SelectedValues", "Selected Surah: " + selectedSurah);
    }

}
