package com.example.clothesapp

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text = findViewById(R.id.textAboutReg)
        val str: String = getResources().getString(R.string.toReg)
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

    fun toNextActivity(view:View)
    {
            val intent = Intent(this, Enter::class.java)
            startActivity(intent)
    }
}