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
import com.example.clothesapp.classesForActivities.Employee
import com.example.clothesapp.classesForActivities.Errors
import com.example.clothesapp.classesForActivities.User
import com.google.firebase.database.*


class Enter : AppCompatActivity() {
    private lateinit var login: EditText
    private lateinit var password: EditText
    private lateinit var enter: Button
    private lateinit var firebaseDataBase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private val listEmployee = ArrayList<Employee>()
    private val listUser = ArrayList<User>()
    private var hasReadError = false
    private var isUnique = true
    private var message=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter)
        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val arguments = intent.extras
        message = arguments?.get("message").toString()
        login = findViewById(R.id.loginEnter)
        password = findViewById(R.id.passwordEnter)
        enter=findViewById(R.id.enterButton)
        firebaseDataBase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDataBase.getReference("EDMT_FIREBASE")
        databaseReference
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.child(message).children) {
                        if(message.equals("employee"))
                            listEmployee.add(ds.getValue(Employee::class.java)!!)
                        else
                            listUser.add(ds.getValue(User::class.java)!!)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    hasReadError = true
                    val text = "Проблемы с подключением к базе данных при чтении."
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(this@Enter, text, duration)
                    toast.show()
                    val intent = Intent(this@Enter, MainActivity::class.java)
                    startActivity(intent)
                }
            })

    }

    private fun checkFieldsValues():Boolean
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
            User.wasLoaded = false
            User.tableName = fieldLogin
            if (message.equals("employee"))
                for (item in listEmployee) {
                    if (fieldLogin.equals(item.login) && fieldPassword.equals(item.password))
                        isUnique = false
                }
            else {
                for (item in listUser) {
                    if (fieldLogin.equals(item.login) && fieldPassword.equals(item.password))
                        isUnique = false
                }
            }
            if (isUnique) {
                if(message.equals("user")) {
                    Toast.makeText(this, "Такого пользователя нет.", Toast.LENGTH_SHORT)
                        .show()
                }
                else {
                    Toast.makeText(this, "Такого сотрудника нет.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        return isUnique
    }

    fun toNextActivityEmployee(view: View) {
        if (!checkFieldsValues()) {
            val intent = Intent(this, MyCatalog::class.java)
            intent.putExtra("message", message);
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}