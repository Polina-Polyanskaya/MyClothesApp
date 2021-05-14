package com.example.clothesapp.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clothesapp.R
import com.example.clothesapp.classesForActivities.Errors
import com.example.clothesapp.classesForActivities.User
import com.google.firebase.database.*

class Registration : AppCompatActivity() {

    private lateinit var login: EditText
    private lateinit var phone: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var registrationButton: Button
    private lateinit var databaseReference: DatabaseReference
    private var isFirstTime = true
    private lateinit var list: ArrayList<User>
    private var hasReadError = false
    private var isUnique = false
    private lateinit var newUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        databaseReference = FirebaseDatabase.getInstance().getReference("EDMT_FIREBASE")
        login = findViewById(R.id.loginReg)
        phone = findViewById(R.id.phoneReg)
        email = findViewById(R.id.emailReg)
        password = findViewById(R.id.passwordReg)
        phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (!s.toString().startsWith("+79")) {
                    if (isFirstTime) {
                        phone.setText("+79$s")
                        isFirstTime = false
                    } else
                        phone.setText("+79")
                    Selection.setSelection(phone.text, phone.text.length)
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
        registrationButton = findViewById(R.id.saveRegButton)
        list = ArrayList()
        databaseReference
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.child("users").children) {
                        list.add(ds.getValue(User::class.java)!!)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    hasReadError = true
                    Toast.makeText(this@Registration, "Проблемы с подключением к базе данных при чтении.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Registration, MainActivity::class.java)
                    startActivity(intent)
                }
            })
    }

    private fun checkFieldsValues(): Boolean {
        val fieldLogin = login.text.toString()
        val fieldPhone = phone.text.toString()
        val fieldEmail = email.text.toString()
        val fieldPassword = password.text.toString()
        isUnique = false
        newUser = User(
            fieldLogin,
            fieldPhone,
            fieldEmail,
            fieldPassword
        )
        var hasErrorInField = false
        val error = Errors()
        val errorInLogin = error.errorsInLogin(fieldLogin)
        val errorInPhone = error.errorsInPhone(fieldPhone)
        val errorInEmail = error.errorsInEmail(fieldEmail)
        val errorInPassword = error.errorsInPassword(fieldPassword)
        if (!errorInLogin.equals("")) {
            login.error = errorInLogin
            hasErrorInField = true
        }
        else if (!errorInPhone.equals(""))
        {
            phone.error=errorInPhone
            hasErrorInField=true
        }
        else if (!errorInEmail.equals(""))
        {
            email.error=errorInEmail
            hasErrorInField=true
        }
        else if (!errorInPassword.equals(""))
        {
            password.error=errorInPassword
            hasErrorInField=true
        }
        if (!hasErrorInField) {
            User.tableName = fieldLogin
            User.wasLoaded=false
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

    private fun writeToDb(newUser: User) {
        writeData(object :
            OnSetDataListener {
            override fun onStart() {
                databaseReference.child("users").push().setValue(newUser).addOnFailureListener {
                    Toast.makeText(this@Registration, "Проблемы с записью в базу данных.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Registration, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        })
    }

    fun toNextActivity(view: View) {
        if (!hasReadError) {
            if (checkFieldsValues()) {
                writeToDb(newUser)
                val intent = Intent(this@Registration, MyCatalog::class.java)
                intent.putExtra("message", "user");
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
