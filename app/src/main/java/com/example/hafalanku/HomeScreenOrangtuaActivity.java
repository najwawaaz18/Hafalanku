package com.example.hafalanku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomeScreenOrangtuaActivity extends AppCompatActivity {

    CardView mutabaahCard, konsultasiCard, viewHafalanCard;
    ImageView profileIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen_ortu);

        mutabaahCard = findViewById(R.id.button_mutabaah);
        konsultasiCard = findViewById(R.id.button_konsultasi);
        viewHafalanCard = findViewById(R.id.view_hafalan_card);

        profileIcon = findViewById(R.id.profile_icon);

        mutabaahCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenOrangtuaActivity.this, UnggahVideoActivity.class);
                startActivity(intent);
            }
        });

        konsultasiCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenOrangtuaActivity.this, KonsultasiActivity.class);
                startActivity(intent);
            }
        });

        viewHafalanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenOrangtuaActivity.this, OrtuViewHafalanActivity.class);
                startActivity(intent);
            }
        });

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenOrangtuaActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}
