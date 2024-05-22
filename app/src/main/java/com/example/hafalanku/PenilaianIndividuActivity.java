package com.example.hafalanku;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
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

public class PenilaianIndividuActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String username, userid;
    private TextView nameTextView;
    private List<String> namaSurahList;
    private HashMap<String, String> hafalanMap;
    ImageView profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.penilaian_individu);

        profileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PenilaianIndividuActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        userid = intent.getStringExtra("userid");

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
                TableLayout tableLayout = findViewById(R.id.table_individu);
//                if (tableLayout.getChildCount() == 0 || tableLayout.getChildAt(0).getId() != R.id.header_individu_row) {
//                    tableLayout.removeAllViews();
//                }

                tableLayout.removeViews(1, tableLayout.getChildCount() - 1);
                
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
        TableRow rowLayout = (TableRow) inflater.inflate(R.layout.table_penilaian_individu_layout, null);

        TextView namaSurahTextView = rowLayout.findViewById(R.id.style_nama_surah_row);
        TextView statusTextView = rowLayout.findViewById(R.id.text_switch);
        SwitchMaterial statusSwitch = rowLayout.findViewById(R.id.status_switch);

        namaSurahTextView.setText(nama_surah);
        statusTextView.setText(status);

        if (status.equals("Lulus")) {
            statusSwitch.setChecked(true);
            statusSwitch.setTextColor(ContextCompat.getColor(this, R.color.blue));
        } else {
            statusSwitch.setChecked(false);
            statusSwitch.setTextColor(ContextCompat.getColor(this, R.color.black));
        }

        TableLayout tableLayout = findViewById(R.id.table_individu);
        tableLayout.addView(rowLayout);

        statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                statusTextView.setText(isChecked ? "Lulus" : "Tidak Lulus");
                updateStatusHafalan(nama_surah, isChecked ? "Lulus" : "Tidak Lulus");

                Toast toast = Toast.makeText(PenilaianIndividuActivity.this, "Berhasil mengubah status hafalan!", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    private void updateStatusHafalan(String nama_surah, String status) {
//        DatabaseReference surahRef = mDatabase.child("Hafalan").child(userid).child("status_hafalan").child(nama_surah);
//        surahRef.setValue(status);

        DatabaseReference surahRef = mDatabase.child("Hafalan").child(userid).child("status_hafalan");

        HashMap<String, Object> updateData = new HashMap<>();
        updateData.put(nama_surah, status);

        surahRef.updateChildren(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Firebase", "Status hafalan berhasil diperbarui");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firebase", "Gagal memperbarui status hafalan", e);
            }
        });
    }

}

