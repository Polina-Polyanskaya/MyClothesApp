package com.example.clothesapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
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
    private lateinit var databaseReference: DatabaseReference
    private var isFirstTime = true
    private lateinit var list: ArrayList<User>
    var hasReadError = false
    var isUnique = false
    lateinit var newUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        getSupportActionBar()?.hide()
        databaseReference = FirebaseDatabase.getInstance().getReference("EDMT_FIREBASE")
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
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
            }
        })
        registrationButton = findViewById(R.id.SaveButton)
        list = ArrayList<User>()
    }

    fun hasAnyErrors(
        fieldLogin: String,
        fieldPhone: String,
        fieldEmail: String,
        fieldPassword: String
    ): Boolean {
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
            login.error =
                "Логин может содержать только символы латницы, цифры и нижнее подчеркивание."
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
            password.error =
                "Пароль может содержать только символы латиницы, цифры и нижнее подчеркивание."
            return true
        }
        return false
    }

    override fun onStart() {
        super.onStart()
        databaseReference
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.child("users").children) {
                        list.add(ds.getValue(User::class.java)!!)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    hasReadError = true
                    val text = "Проблемы с подключением к базе данных при чтении."
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(this@Registration, text, duration)
                    toast.show()
                    val intent = Intent(this@Registration, MainActivity::class.java)
                    startActivity(intent)
                }
            })
    }

    fun saveData(): Boolean {
        val fieldLogin = login.text.toString()
        val fieldPhone = phone.text.toString()
        val fieldEmail = email.text.toString()
        val fieldPassword = password.text.toString()
        isUnique = false
        newUser = User(fieldLogin, fieldPhone, fieldEmail, fieldPassword)
        if (!hasAnyErrors(fieldLogin, fieldPhone, fieldEmail, fieldPassword)) {
            isUnique = true
            for (item in list) {
                if (newUser.equals(item))
                    isUnique = false
            }
            if (!isUnique) {
                val text = "Такой пользователь уже есть."
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(this, text, duration)
                toast.show()
            }
        }
        return isUnique
    }

    interface OnSetDataListener {
        fun onStart()
    }

    fun writeData(listener: OnSetDataListener) {
        listener.onStart()
    }

    private fun mCheckInforInServer(newUser: User) {
        writeData(object : OnSetDataListener {
            override fun onStart() {
                databaseReference.child("users").push().setValue(newUser).addOnFailureListener {
                    val text = "Проблемы с записью в базу данных."
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(this@Registration, text, duration)
                    toast.show()
                    val intent = Intent(this@Registration, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        })
    }

    fun toNextActivity(view: View) {
        if (!hasReadError) {
            if (saveData()) {
                mCheckInforInServer(newUser)
                val intent = Intent(this@Registration, MyCatalog::class.java)
                startActivity(intent)
            }
        }
    }
}
