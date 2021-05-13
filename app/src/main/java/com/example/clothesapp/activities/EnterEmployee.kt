package com.example.clothesapp.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clothesapp.R
import com.example.clothesapp.classesForActivities.Employee
import com.example.clothesapp.classesForActivities.Errors
import com.google.firebase.database.*


class EnterEmployee : AppCompatActivity() {
    private lateinit var login: EditText
    private lateinit var password: EditText
    private lateinit var enter: Button
    private lateinit var firebaseDataBase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private val list: ArrayList<Employee> = ArrayList<Employee>()
    private var hasReadError = false
    private var isUnique = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_employee)
        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        login = findViewById(R.id.loginEnterEmployee)
        password = findViewById(R.id.passwordEnterEmployee)
        enter=findViewById(R.id.enterButtonEmployee)
        firebaseDataBase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDataBase.getReference("EDMT_FIREBASE")
        databaseReference
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.child("employees").children) {
                        list.add(ds.getValue(Employee::class.java)!!)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    hasReadError = true
                    val text = "Проблемы с подключением к базе данных при чтении."
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(this@EnterEmployee, text, duration)
                    toast.show()
                    val intent = Intent(this@EnterEmployee, MainActivity::class.java)
                    startActivity(intent)
                }
            })
    }

    private fun saveEnterData():Boolean
    {
        val fieldLogin = login.text.toString()
        val fieldPassword = password.text.toString()
        isUnique=true
        var hasErrorInField=false
        val error= Errors()
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
                val text = "Такого сотрудника нет."
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(this, text, duration)
                toast.show()
            }
        }
        return isUnique
    }

    fun toNextActivityEmployee(view: View) {
        if (!saveEnterData()) {
            val intent = Intent(this, MyCatalog::class.java)
            intent.putExtra("message", "employee");
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}