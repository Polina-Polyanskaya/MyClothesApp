package com.example.clothesapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*


class MyCatalog : AppCompatActivity() {
    private lateinit var menuButton: ImageButton
    private lateinit var plusButton: ImageButton
    private var message: String = ""
    private lateinit var databaseReference: DatabaseReference
    private val list: ArrayList<Page> = ArrayList<Page>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager:RecyclerView.LayoutManager
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_catalog)
        getSupportActionBar()?.hide()
        println("4444444444444444444444")
        recyclerView=findViewById(R.id.recyclerView)
        layoutManager= LinearLayoutManager(this)
        recyclerView.layoutManager=layoutManager
        recyclerView.hasFixedSize()
        plusButton=findViewById(R.id.plus)
        val arguments = intent.extras
        message=arguments?.get("message").toString()
        if(message.equals("user"))
            plusButton.visibility = View.GONE
        menuButton = findViewById(R.id.imageButton)
        databaseReference = FirebaseDatabase.getInstance().getReference("EDMT_FIREBASE")
        var query=databaseReference.child("photos")
        query.addListenerForSingleValueEvent(object :ValueEventListener
        {
            override fun onCancelled(error: DatabaseError) {
                val text = "Проблемы с подключением к базе данных при чтении."
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(this@MyCatalog, text, duration)
                toast.show()
                val intent = Intent(this@MyCatalog, MainActivity::class.java)
                startActivity(intent)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    list.add(ds.getValue(Page::class.java)!!)
                }
                recyclerViewAdapter=RecyclerViewAdapter(list,this@MyCatalog)
                recyclerView.adapter=recyclerViewAdapter
                recyclerViewAdapter.notifyDataSetChanged()
            }

        })
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