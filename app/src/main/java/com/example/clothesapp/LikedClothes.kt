package com.example.clothesapp

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
        recyclerViewAdapterCatalog=RecyclerViewAdapterCatalog(manager.readDbData(),this@LikedClothes,R.layout.one_liked_photo)
        recyclerView.adapter=recyclerViewAdapterCatalog
        recyclerViewAdapterCatalog.notifyDataSetChanged()
    }
}