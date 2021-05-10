package com.example.clothesapp.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clothesapp.R
import com.example.clothesapp.classesForActivities.Errors
import com.example.clothesapp.classesForActivities.User
import com.google.firebase.database.*

class EnterUsers : AppCompatActivity() {

    private lateinit var firebaseDataBase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var login: EditText
    private lateinit var password: EditText
    private lateinit var enter: Button
    private val list: ArrayList<User> = ArrayList()
    private var hasReadError = false
    private var isUnique = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_enter_users)
        supportActionBar?.hide()
        firebaseDataBase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDataBase.getReference("EDMT_FIREBASE")
        login = findViewById(R.id.LoginEnter)
        password = findViewById(R.id.PasswordEnter)
        enter = findViewById(R.id.EnterButton2)
        databaseReference
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.child("users").children) {
                        list.add(ds.getValue(User::class.java)!!)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    hasReadError = true
                    Toast.makeText(
                        this@EnterUsers,
                        "Проблемы с записью в базу данных.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@EnterUsers, MainActivity::class.java)
                    startActivity(intent)
                }
            })
    }

    private fun saveEnterData(): Boolean {
        val fieldLogin = login.text.toString()
        val fieldPassword = password.text.toString()
        isUnique = true
        var hasErrorInField=false
        val error=Errors()
        val errorInLogin=error.errorsInLogin(fieldLogin)
        val errorInPassword=error.errorsInPassword(fieldPassword)
        if(!errorInLogin.equals("")) {
            login.error = errorInLogin
            hasErrorInField=true
        }
        else if(!errorInPassword.equals(""))
        {
            password.error=errorInPassword
            hasErrorInField=true
        }
        if (!hasErrorInField) {
            for (item in list) {
                if (fieldLogin.equals(item.login) && fieldPassword.equals(item.password))
                    isUnique = false
            }
            if (isUnique) {
                Toast.makeText(this@EnterUsers, "Такого пользователя нет.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return isUnique
    }

    fun toNextActivity(view: View) {
        if (!saveEnterData()) {
            val intent = Intent(this, MyCatalog::class.java)
            intent.putExtra("message", "user");
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}
