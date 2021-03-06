package com.example.clothesapp.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.icu.lang.UCharacter.IndicPositionalCategory.LEFT
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clothesapp.R
import com.example.clothesapp.classesForActivities.*
import com.example.clothesapp.db.MyDbManager
import com.google.firebase.database.*

class MyCatalog : AppCompatActivity(),
    Swipe.SwipeControllerListener {
    private lateinit var menuButton: ImageButton
    private lateinit var plusButton: ImageButton
    private lateinit var trashCanButton: ImageButton
    private var message: String = ""
    private lateinit var infoButton:ImageButton
    private lateinit var databaseReference: DatabaseReference
    private val list: ArrayList<Page> = ArrayList<Page>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager:RecyclerView.LayoutManager
    private lateinit var recyclerViewAdapterCatalog: RecyclerViewAdapterCatalog
    private lateinit var likedClothes:ImageButton
    private lateinit var dialog: DialogFragment
    private var manager = MyDbManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_catalog)
        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        recyclerView = findViewById(R.id.recyclerViewInCatalog)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        plusButton = findViewById(R.id.plusButton)
        infoButton = findViewById(R.id.infoButton)
        likedClothes = findViewById(R.id.likedClothesButton)
        val arguments = intent.extras
        message = arguments?.get("message").toString()
        if (message.equals("user"))
            plusButton.visibility = View.GONE
        else
            likedClothes.visibility = View.GONE
        menuButton = findViewById(R.id.sortButton)
        trashCanButton = findViewById(R.id.trashCanButton)
        databaseReference = FirebaseDatabase.getInstance().getReference("EDMT_FIREBASE")
        manager.openDb()
        if(!User.wasLoaded && !message.equals("employee")) {
            val query = databaseReference.child(User.tableName)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@MyCatalog,
                        "???????????????? ?? ???????????????????????? ?? ???????? ???????????? ?????? ????????????.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@MyCatalog, MainActivity::class.java)
                    startActivity(intent)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        val page=ds.getValue(Page::class.java)!!
                        manager.insertToDb(page.path!!,page.comment!!)
                    }
                    User.wasLoaded = true
                }
            })
        }
        val query2 = databaseReference.child("photos")
        query2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MyCatalog, "???????????????? ?? ???????????????????????? ?? ???????? ???????????? ?????? ????????????.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MyCatalog, MainActivity::class.java)
                startActivity(intent)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    list.add(ds.getValue(Page::class.java)!!)
                }
                recyclerViewAdapterCatalog =
                    RecyclerViewAdapterCatalog(
                        message,
                        list,
                        this@MyCatalog,
                        R.layout.catalog_photos,
                        arrayListOf()
                    )
                recyclerView.adapter = recyclerViewAdapterCatalog
                recyclerViewAdapterCatalog.notifyDataSetChanged()
                if(message.equals("employee")) {
                    val simpleCallback: ItemTouchHelper.SimpleCallback =
                        Swipe(
                            0,
                            LEFT,
                            this@MyCatalog
                        )
                    ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView)
                }
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
            "????????????????",
            "????????????",
            "??????????",
            "??????????",
            "?????????????? ????????????",
            "??????????"
        )
        val checkedTypes= booleanArrayOf(
            false,
            false,
            false,
            false,
            false,
            false
        )
        builder.setMultiChoiceItems(
            types,
            checkedTypes
        ) { _, _, _ ->
        }
        builder.setTitle("???????????????? ?????????????????? ?????? ????????????.")
        builder.setPositiveButton(
            "??????????"
        ) { dialog, which ->
            val clearList=ArrayList<Page>()
            for(item in list)
            {
                val index=types.indexOf(item.type)
                if(checkedTypes.get(index))
                    clearList.add(item)
            }
            recyclerViewAdapterCatalog=
                RecyclerViewAdapterCatalog(
                    message,
                    clearList,
                    this@MyCatalog,
                    R.layout.catalog_photos,
                    list
                )
            recyclerView.adapter=recyclerViewAdapterCatalog
            recyclerViewAdapterCatalog.notifyDataSetChanged()
        }
        builder.setNeutralButton(
            "????????????"
        ) { _, _ ->
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun deleteSort(view:View)
    {
        val intent = Intent(this@MyCatalog, MyCatalog::class.java)
        intent.putExtra("message", message)
        startActivity(intent)
    }

    fun toLikedClothes(view: View)
    {
        val intent = Intent(this@MyCatalog, LikedClothes::class.java)
        startActivity(intent)
    }

    fun showInfo(view: View) {
        dialog= InfoDialog()
        dialog.show(supportFragmentManager,"custom")
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this@MyCatalog)
        builder.setTitle("???? ?????????? ???????????? ???????????")
        builder.setPositiveButton(
            "????"
        ) { _, _ ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        builder.setNeutralButton(
            "??????"
        ) { _, _ ->
        }
        val dialog = builder.create()
        dialog.show()
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
