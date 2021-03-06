package com.example.clothesapp.activities

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import com.example.clothesapp.classesForActivities.Page
import com.example.clothesapp.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*


class
AddClothes : AppCompatActivity() {
    private lateinit var clothesSpinner: Spinner
    private var typeOfClothes = ""
    private lateinit var addPhotoButton: Button
    private lateinit var image: ImageView
    private var wasAdded = false
    private lateinit var addPageButton: Button
    private lateinit var databaseReference: DatabaseReference
    private lateinit var filePath: Uri
    private var storageReference: StorageReference? = null
    private var comment: String = ""
    private lateinit var commentField: EditText
    private val path: String = "photos/" + UUID.randomUUID().toString()
    private val PICK_IMAGE_REQUEST = 1234
    private val PERMISSION_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_clothes)
        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        addPhotoButton = findViewById(R.id.addPhotoButton)
        addPageButton = findViewById(R.id.addButton)
        databaseReference = FirebaseDatabase.getInstance().getReference("EDMT_FIREBASE")
        val types = arrayOf(
            "Футболка",
            "Платье",
            "Кофта",
            "Штаны",
            "Верхняя одежда",
            "Обувь"
        )
        storageReference = FirebaseStorage.getInstance().getReference()
        clothesSpinner = findViewById(R.id.spinner)
        commentField = findViewById(R.id.comment)
        wasAdded = false
        image = findViewById(R.id.image)
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        clothesSpinner.setAdapter(adapter)
        clothesSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?,
                position: Int, id: Long
            ) {
                typeOfClothes = types[position]
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }
    }

    fun addPage(view: View) {
        val progressDialog = ProgressDialog(this)
        val progressBar = ProgressBar(this)
        if (wasAdded) {
            comment = commentField.text.toString()
            val page = Page(
                path,
                typeOfClothes,
                comment
            )
            databaseReference.child("photos").push().setValue(page).addOnFailureListener {
                Toast.makeText(this@AddClothes, "Проблемы с записью в базу данных.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@AddClothes, MainActivity::class.java)
                startActivity(intent)
            }
            progressDialog.setTitle("Грузится")
            progressDialog.show()
            val ref = storageReference?.child(path)
            ref?.putFile(filePath)
                ?.addOnProgressListener { task ->
                    val progress =
                        100.0 * task.bytesTransferred / task.totalByteCount
                    progressBar.setProgress(progress.toInt(), true)
                }
                ?.addOnSuccessListener { taskSnapshot ->
                    progressDialog.dismiss()
                    Toast.makeText(this@AddClothes, "Загрузилась.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@AddClothes, MyCatalog::class.java)
                    intent.putExtra("message", "employee")
                    startActivity(intent)
                }
                ?.addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(this@AddClothes, "Проблемы с записью в базу данных.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@AddClothes, MainActivity::class.java)
                    startActivity(intent)
                }
        } else {
            Toast.makeText(this, "Фотография не была добавлена", Toast.LENGTH_SHORT).show()
        }
    }

    fun addPhoto(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                pickImageFromGallery()
            }
        } else {
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), PICK_IMAGE_REQUEST)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    pickImageFromGallery()
                } else {
                    Toast.makeText(this, "Доступ запрещен", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST && data != null && data.data != null) {
            filePath = data.data!!
            image.setImageURI(filePath)
            wasAdded = true
        }
    }

}