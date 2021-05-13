package com.example.clothesapp.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.clothesapp.R

class EnterTwoTypes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_two_types)
        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    fun toNextActivityUsers(view: View) {
        val intent = Intent(this, EnterUsers::class.java)
        startActivity(intent)
    }

    fun toNextActivityEmployee(view:View) {
        val intent = Intent(this, EnterEmployee::class.java)
        startActivity(intent)
    }
}