package com.example.hafalanku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomeScreenGuruActivity extends AppCompatActivity {

    CardView mutabaahCard, setoranCard, penilaianCard;
    ImageView profileIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen_guru);

        mutabaahCard = findViewById(R.id.button_mutabaah);
        profileIcon = findViewById(R.id.profile_icon);

        mutabaahCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenGuruActivity.this, MutabaahAllActivity.class);
                startActivity(intent);
            }
        });

        setoranCard = findViewById(R.id.button_setoran);

        setoranCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenGuruActivity.this, SetoranSiswaActivity.class);
                startActivity(intent);
            }
        });

        penilaianCard = findViewById(R.id.button_penilaian);

        penilaianCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenGuruActivity.this, PenilaianAllActivity.class);
                startActivity(intent);
            }
        });

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenGuruActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

    }
}
