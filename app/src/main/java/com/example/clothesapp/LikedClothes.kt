package com.example.clothesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clothesapp.db.myDbManager

class LikedClothes : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerViewAdapterCatalog: RecyclerViewAdapterCatalog
    private lateinit var manager: myDbManager

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
        recyclerViewAdapterCatalog=RecyclerViewAdapterCatalog("user",manager.readDbData(),this@LikedClothes,R.layout.single_view)
        recyclerView.adapter=recyclerViewAdapterCatalog
        recyclerViewAdapterCatalog.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MyCatalog::class.java)
        intent.putExtra("message", "user")
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.closeDb()
    }
}