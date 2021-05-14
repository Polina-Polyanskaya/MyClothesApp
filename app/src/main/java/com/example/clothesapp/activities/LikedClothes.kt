package com.example.clothesapp.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.icu.lang.UCharacter.IndicPositionalCategory.LEFT
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clothesapp.R
import com.example.clothesapp.classesForActivities.Page
import com.example.clothesapp.classesForActivities.RecyclerViewAdapterCatalog
import com.example.clothesapp.classesForActivities.Swipe
import com.example.clothesapp.classesForActivities.User
import com.example.clothesapp.db.MyDbManager
import com.google.firebase.database.*

class LikedClothes : AppCompatActivity(),
    Swipe.SwipeControllerListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerViewAdapterCatalog: RecyclerViewAdapterCatalog
    private lateinit var manager: MyDbManager
    private lateinit var listOfPages: ArrayList<Page>
    private lateinit var databaseReference: DatabaseReference
    private val list: ArrayList<Page> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liked_clothes)
        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        recyclerView = findViewById(R.id.recyclerViewInLikedClothes)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        manager = MyDbManager(this)
        manager.openDb()
        listOfPages = manager.readDbData()
        databaseReference = FirebaseDatabase.getInstance().getReference("EDMT_FIREBASE")
        val query = databaseReference.child("photos")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@LikedClothes,
                    "Проблемы с подключением к базе данных при чтении.",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this@LikedClothes, MainActivity::class.java)
                startActivity(intent)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children)
                    list.add(ds.getValue(Page::class.java)!!)
            }
        })
        recyclerViewAdapterCatalog =
            RecyclerViewAdapterCatalog(
                "UserCanDelete",
                listOfPages,
                this@LikedClothes,
                R.layout.catalog_photos,
                arrayListOf()
            )
        recyclerView.adapter = recyclerViewAdapterCatalog
        recyclerViewAdapterCatalog.notifyDataSetChanged()
        val simpleCallback: ItemTouchHelper.SimpleCallback =
            Swipe(
                0,
                LEFT,
                this@LikedClothes
            )
        ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MyCatalog::class.java)
        intent.putExtra("message", "user")
        startActivity(intent)
    }

    fun toCatalog(view: View) {
        val intent = Intent(this, MyCatalog::class.java)
        intent.putExtra("message", "user")
        startActivity(intent)
    }

    fun reload(view: View) {
        for (i in listOfPages) {
            var checker = true
            for (j in list) {
                if (i.path.equals(j.path))
                    checker = false
            }
            if (checker) {
                manager.deleteString(i.path!!)
                val query = databaseReference.child(User.tableName)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@LikedClothes,
                            "Проблемы с подключением к базе данных при чтении.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@LikedClothes, MainActivity::class.java)
                        startActivity(intent)
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (ds in snapshot.children) {
                            val page=ds.getValue(Page::class.java)!!
                            if(page.path!!.equals(i.path!!)) {
                                ds.ref.removeValue()
                                break
                            }
                        }
                    }
                })
            }
        }
        listOfPages = manager.readDbData()
        recyclerViewAdapterCatalog =
            RecyclerViewAdapterCatalog(
                "UserCanDelete",
                listOfPages,
                this@LikedClothes,
                R.layout.catalog_photos,
                arrayListOf()
            )
        recyclerView.adapter = recyclerViewAdapterCatalog
        recyclerViewAdapterCatalog.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.closeDb()
    }

    override fun swipe(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is RecyclerViewAdapterCatalog.MyViewHolder) {
            recyclerViewAdapterCatalog.removeItem(position)
        }
    }
}