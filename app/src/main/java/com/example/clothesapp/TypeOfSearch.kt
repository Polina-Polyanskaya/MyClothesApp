package com.example.clothesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class TypeOfSearch : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type_of_search)
        getSupportActionBar()?.hide()
    }
}