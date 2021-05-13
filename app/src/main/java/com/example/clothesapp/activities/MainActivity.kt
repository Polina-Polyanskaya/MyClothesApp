package com.example.clothesapp.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.clothesapp.R
import com.example.clothesapp.db.DataBaseInfo
import com.example.clothesapp.db.MyDbManager

class MainActivity : AppCompatActivity() {

    private lateinit var text: TextView
    private var manager=MyDbManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text = findViewById(R.id.textAboutReg)
        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val str: String = resources.getString(R.string.toReg)
        val spannableString = SpannableString(str)
        val clickableSpan = object : ClickableSpan() {

            override fun onClick(view: View) {
                val intent= Intent(this@MainActivity, Registration::class.java)
                startActivity(intent)
            }
        }
        val phrase = "зарегистрируйтесь"
        val start = str.indexOf(phrase)
        val end = start + phrase.length
        spannableString.setSpan(
            clickableSpan,
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        text.movementMethod = LinkMovementMethod.getInstance()
        text.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    fun toNextActivity(view: View) {
        manager.openDb()
        manager.clearDbData()
        val intent = Intent(this, EnterTwoTypes::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.closeDb()
    }

    override fun onBackPressed() {

    }
}