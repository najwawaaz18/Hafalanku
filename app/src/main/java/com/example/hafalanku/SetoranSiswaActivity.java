package com.example.hafalanku;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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
    Button buttonSelesai;
    String selectedStudentName = "";
    String selectedSurah = "";
    String selectedStudentId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setoran_siswa);

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

        String statusHafalan = "Tidak Lulus";

        DatabaseReference hafalanRef = FirebaseDatabase.getInstance("https://hafalanku-c0546-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("Hafalan").child(selectedStudentId);
        hafalanRef.child("nama").setValue(selectedStudentName);
        hafalanRef.child("status_hafalan").child(selectedSurah).setValue(statusHafalan);

        Toast toast = Toast.makeText(SetoranSiswaActivity.this, "Berhasil menambahkan data setoran hafalan!", Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent(SetoranSiswaActivity.this, HomeScreenGuruActivity.class);
        startActivity(intent);

        Log.d("SelectedValues", "Selected Student Name: " + selectedStudentName);
        Log.d("SelectedValues", "Selected Surah: " + selectedSurah);

    }
}
