package com.example.clothesapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.util.regex.Pattern

class EnterEmployee : AppCompatActivity() {
    private lateinit var login: EditText
    private lateinit var password: EditText
    private lateinit var enter: Button
    private lateinit var firebaseDataBase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private val list: ArrayList<Employee> = ArrayList<Employee>()
    var hasReadError = false
    var isUnique = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_employee)
        getSupportActionBar()?.hide();
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


    fun hasAnyErrors(fieldLogin: String, fieldPassword: String): Boolean {
        //логин
        if (fieldLogin.isEmpty()) {
            login.error = "Логин не может быть пустым."
            return true
        }
        if (!(fieldLogin.length >= 5 && fieldLogin.length <= 30)) {
            login.error = "Логин не может быть длиной меньше 5 или больше 30"
            return true
        }
        if (!Pattern.compile("^[_A-z]+$").matcher(fieldLogin).matches()) {
            login.error = "Логин может содержать только символы латницы и нижнее подчеркивание."
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

    fun saveEnterData():Boolean
    {
        val fieldLogin = login.text.toString()
        val fieldPassword = password.text.toString()
        isUnique=true
        if (!hasAnyErrors(fieldLogin, fieldPassword)) {
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