package com.example.hafalanku;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

public class BaseConfiguration extends Application {

    private FirebaseDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();

        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
    }

    public DatabaseReference getDatabaseReference() {
        return database.getReference();
    }
}

