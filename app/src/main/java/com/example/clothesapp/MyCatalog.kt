package com.example.clothesapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MyCatalog : AppCompatActivity() {
    private lateinit var menuButton: ImageButton
    private lateinit var plusButton: ImageButton
    private var message: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_catalog)
        getSupportActionBar()?.hide()
        plusButton=findViewById(R.id.plus)
        val arguments = intent.extras
        message=arguments?.get("message").toString()
        if(message.equals("user"))
            plusButton.visibility = View.GONE
        menuButton = findViewById(R.id.imageButton)
    }

    fun add(view:View)
    {
        val intent = Intent(this, AddClothes::class.java)
        startActivity(intent)
    }

    fun sort(view: View) {
        val builder = AlertDialog.Builder(this@MyCatalog)
        val types = arrayOf(
            "Футболка",
            "Платье",
            "Штаны",
            "Куртка",
            "Пальто"
        )
        val checkedTypes= booleanArrayOf(
            false,
            false,
            false,
            false,
            false
        )
        builder.setMultiChoiceItems(
            types,
            checkedTypes
        ) { dialog, item, isChecked ->
        }
        builder.setTitle("Выберите категории для поиска.")
        builder.setPositiveButton(
            "Поиск"
        ) { dialog, which ->
        }
        builder.setNeutralButton(
            "Отмена"
        ) { dialog, which ->
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}