package com.example.hafalanku;

import android.content.Intent;
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

public class MutabaahAllActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    ImageView profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mutabaah_all);

        profileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MutabaahAllActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        mDatabase = FirebaseDatabase.getInstance("https://hafalanku-c0546-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        DatabaseReference dataRef = mDatabase.child("Users");

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                TableLayout tableLayout = findViewById(R.id.table_mutabaah_all);
                if (tableLayout.getChildCount() == 0 || tableLayout.getChildAt(0).getId() != R.id.header_all_row) {
                    tableLayout.removeAllViews();
                }

                int index = 1;
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String no = String.valueOf(index);
                    String nis = String.valueOf(childSnapshot.child("userid").getValue());
                    String nama = childSnapshot.getKey();
                    String type = String.valueOf(childSnapshot.child("usertype").getValue());

                    if (!type.equals("guru")) {
                        addRowToTable(no, nis, nama);
                        index += 1;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Failed to read data", error.toException());
            }
        });
    }

    private void addRowToTable(String no, String nis, String nama) {
        LayoutInflater inflater = LayoutInflater.from(this);
        TableRow rowLayout = (TableRow) inflater.inflate(R.layout.table_mutabaah_all_row_layout, null);

        ((TextView) rowLayout.findViewById(R.id.style_no_row)).setText(no);
        ((TextView) rowLayout.findViewById(R.id.style_nama_row)).setText(nama);

        rowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("Clicked UserID", nis);
                Intent intent = new Intent(MutabaahAllActivity.this, MutabaahVideoActivity.class);
                intent.putExtra("username", nama);
                intent.putExtra("userid", nis);
                startActivity(intent);
            }
        });

        TableLayout tableLayout = findViewById(R.id.table_mutabaah_all);
        tableLayout.addView(rowLayout);
    }

}
