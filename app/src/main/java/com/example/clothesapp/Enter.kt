package com.example.clothesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.*
import java.util.regex.Pattern

class Enter : AppCompatActivity() {

    private lateinit var firebaseDataBase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var login: EditText
    private lateinit var password: EditText
    private lateinit var enter: Button
    private var isUnique = true
    private val list: ArrayList<User> = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter)
        firebaseDataBase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDataBase.getReference("EDMT_FIREBASE")
        login = findViewById(R.id.LoginEnter)
        password = findViewById(R.id.PasswordEnter)
        enter=findViewById(R.id.EnterButton2)
    }

    fun hasAnyErrors(fieldLogin: String, fieldPassword: String): Boolean {
        //логин
        if (fieldLogin.isEmpty()) {
            login.error = "Логин не может быть пустым."
            return true
        }
        if (!(fieldLogin.length >= 5 && fieldLogin.length <= 15)) {
            login.error = "Логин не может быть длиной меньше 5 или больше 15"
            return true
        }
        if (!Pattern.compile("^[_A-z0-9]+$").matcher(fieldLogin).matches()) {
            login.error = "Логин может содержать только символы латницы, цифры и нижнее подчеркивание."
            return true
        }
        //пароль
        if (fieldPassword.isEmpty()) {
            password.error = "Пароль не может быть пустым."
            return true
        }
        if (!(fieldPassword.length >= 8 && fieldPassword.length <= 15)) {
            password.error = "Пароль не может быть длиной меньше 8 или больше 15."
            return true
        }
        if (!Pattern.compile("^[_A-z0-9]+$").matcher(fieldPassword).matches()) {
            password.error = "Пароль может содержать только символы латиницы, цифры и нижнее подчеркивание."
            return true
        }
        return false
    }

    public override fun onStart()
    {
        super.onStart()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.child("users").children) {
                    list.add( ds.getValue(User::class.java)!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Failed to read value.", error.toException())
            }
        })
    }

    fun saveEnterData()
    {
        val fieldLogin = login.text.toString()
        val fieldPassword = password.text.toString()
        isUnique = true
        if (!hasAnyErrors(fieldLogin, fieldPassword)) {
            println(fieldPassword.toString())
            for (item in list) {
                if (fieldLogin.equals(item.login) && fieldPassword.equals(item.password))
                    isUnique = false
            }
            if (isUnique) {
                val text = "Такого пользователя нет."
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
            }
        }
    }

    fun toNextActivity(view:View) {
        saveEnterData()
        if (!isUnique) {
            val intent = Intent(this, MyCatalog::class.java)
            startActivity(intent)
        }
    }
}
