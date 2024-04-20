package com.example.hafalanku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomeScreenGuruActivity extends AppCompatActivity {

    CardView mutabaahCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen_guru);

        mutabaahCard = findViewById(R.id.button_mutabaah);

        mutabaahCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenGuruActivity.this, VideoActivity.class);
                startActivity(intent);
            }
        });
    }
}
