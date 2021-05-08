package com.example.clothesapp

import android.content.Intent
import android.icu.lang.UCharacter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clothesapp.db.myDbManager
import com.google.firebase.database.*

class LikedClothes : AppCompatActivity(),SwipeController.SwipeControllerListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerViewAdapterCatalog: RecyclerViewAdapterCatalog
    private lateinit var manager: myDbManager
    private lateinit var listOfPages:ArrayList<Page>
    private lateinit var databaseReference: DatabaseReference
    private val list: ArrayList<Page> = ArrayList<Page>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liked_clothes)
        getSupportActionBar()?.hide()
        recyclerView=findViewById(R.id.recyclerViewInLikedClothes)
        layoutManager= LinearLayoutManager(this)
        recyclerView.layoutManager=layoutManager
        recyclerView.hasFixedSize()
        manager=myDbManager(this)
        manager.openDb()
        listOfPages=manager.readDbData()
        databaseReference = FirebaseDatabase.getInstance().getReference("EDMT_FIREBASE")
        var query = databaseReference.child("photos")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                val text = "Проблемы с подключением к базе данных при чтении."
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(this@LikedClothes, text, duration)
                toast.show()
                val intent = Intent(this@LikedClothes, MainActivity::class.java)
                startActivity(intent)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children)
                    list.add(ds.getValue(Page::class.java)!!)
            }
        })
        recyclerViewAdapterCatalog=RecyclerViewAdapterCatalog("notUser",listOfPages,this@LikedClothes,R.layout.single_view,
            arrayListOf())
        recyclerView.adapter=recyclerViewAdapterCatalog
        recyclerViewAdapterCatalog.notifyDataSetChanged()
        var simpleCallback: ItemTouchHelper.SimpleCallback =
            SwipeController(0, UCharacter.IndicPositionalCategory.LEFT, this@LikedClothes)
        ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MyCatalog::class.java)
        intent.putExtra("message", "user")
        startActivity(intent)
    }

    fun toCatalog(view: View)
    {
        val intent = Intent(this, MyCatalog::class.java)
        intent.putExtra("message", "user")
        startActivity(intent)
    }

    fun reload(view: View)
    {
        println("AAA "+listOfPages.size)
        for (i in listOfPages) {
            var checker = true
            for (j in list) {
                if (i.path.equals(j.path))
                    checker = false
            }
            if(checker) {
                println("OOOOOOOOOOOOOOOOO")
                manager.deleteString(i.path!!)
            }
        }
        listOfPages = manager.readDbData()
        println("AAA " + listOfPages.size)
        recyclerViewAdapterCatalog =
            RecyclerViewAdapterCatalog("notUser", listOfPages, this@LikedClothes, R.layout.single_view,
                arrayListOf())
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