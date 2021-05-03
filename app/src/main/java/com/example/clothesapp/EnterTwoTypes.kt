package com.example.clothesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class EnterTwoTypes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_two_types)
        getSupportActionBar()?.hide();
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