package com.example.clothesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MyCatalog : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_catalog)
        getSupportActionBar()?.hide()
    }

    fun toNextActivitySearch(view: View) {
        val intent = Intent(this, TypeOfSearch::class.java)
        startActivity(intent)
    }
}