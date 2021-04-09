package com.example.clothesapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.util.regex.Pattern


class Registration : AppCompatActivity() {

    private lateinit var login: EditText
    private lateinit var phone: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var registrationButton: Button
    private lateinit var firebaseDataBase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private var isFirstTime = true
    private val list: ArrayList<User> = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        getSupportActionBar()?.hide()
        firebaseDataBase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDataBase.getReference("EDMT_FIREBASE")
        login = findViewById(R.id.Login)
        phone = findViewById(R.id.TelephoneEdit)
        email = findViewById(R.id.EmailEdit)
        password = findViewById(R.id.PasswordEdit)
        phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (!s.toString().startsWith("+79")) {
                    if (isFirstTime) {
                        phone.setText("+79$s")
                        isFirstTime = false
                    } else
                        phone.setText("+79")
                    Selection.setSelection(phone.getText(), phone.getText().length)
                }
            }

            override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                    s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })
        registrationButton = findViewById(R.id.SaveButton)
    }

    fun hasAnyErrors(fieldLogin: String, fieldPhone: String, fieldEmail: String, fieldPassword: String): Boolean {
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
        //телефон
        if (fieldPhone.length != 12) {
            phone.error = "Номер телефона не может быть длиной не равной 12."
            return true
        }
        if (!Pattern.compile("^[0-9]+$").matcher(fieldPhone.substring(3)).matches()) {
            phone.error = "Номер телефона может содержать только цифры и один плюс в начале."
            return true
        }
        //почта
        if (fieldEmail.isEmpty()) {
            email.error = "Почта не может быть пустой."
            return true
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(fieldEmail).matches()) {
            email.error = "Это не адрес электронной почты."
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

    fun saveData():Boolean {
        val fieldLogin = login.text.toString()
        val fieldPhone = phone.text.toString()
        val fieldEmail = email.text.toString()
        val fieldPassword = password.text.toString()
        var isUnique = false
        if (!hasAnyErrors(fieldLogin, fieldPhone, fieldEmail, fieldPassword)) {
            isUnique=true
            val newUser: User = User(fieldLogin, fieldPhone, fieldEmail, fieldPassword)
            for (item in list) {
                if (newUser.equals(item))
                    isUnique = false
            }
            if (isUnique) {
                databaseReference.child("users").push().setValue(newUser)
            } else {
                val text = "Такой пользователь уже есть."
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
            }
        }
        return isUnique
    }

    fun toNextActivity(view:View) {
        if(saveData()){
            val intent = Intent(this, MyCatalog::class.java)
            startActivity(intent)
        }
    }
}
