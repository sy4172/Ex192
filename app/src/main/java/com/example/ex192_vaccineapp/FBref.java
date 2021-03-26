package com.example.ex192_vaccineapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FBref {

    public static final FirebaseDatabase FBDB = FirebaseDatabase.getInstance();

    public static DatabaseReference refStudents = FBDB.getReference("Students");
}

