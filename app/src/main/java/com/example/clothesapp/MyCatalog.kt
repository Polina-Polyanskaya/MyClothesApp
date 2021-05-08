package com.example.clothesapp

import android.content.Intent
import android.icu.lang.UCharacter.IndicPositionalCategory.LEFT
import android.os.Bundle
import android.os.SystemClock
import android.view.GestureDetector
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class MyCatalog : AppCompatActivity(),SwipeController.SwipeControllerListener{
    private lateinit var menuButton: ImageButton
    private lateinit var plusButton: ImageButton
    private lateinit var trashCanButton: ImageButton
    private var message: String = ""
    private lateinit var databaseReference: DatabaseReference
    private val list: ArrayList<Page> = ArrayList<Page>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager:RecyclerView.LayoutManager
    private lateinit var recyclerViewAdapterCatalog: RecyclerViewAdapterCatalog
    private lateinit var likedClothes:ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_catalog)
        getSupportActionBar()?.hide()
        recyclerView = findViewById(R.id.recyclerViewInCatalog)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.hasFixedSize()
        plusButton = findViewById(R.id.plus)
        likedClothes = findViewById(R.id.LikedClothes)
        val arguments = intent.extras
        message = arguments?.get("message").toString()
        if (message.equals("user"))
            plusButton.visibility = View.GONE
        else
            likedClothes.visibility=View.GONE
        menuButton = findViewById(R.id.imageButton)
        trashCanButton = findViewById(R.id.trashCanButton)
        databaseReference = FirebaseDatabase.getInstance().getReference("EDMT_FIREBASE")
        var query = databaseReference.child("photos")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
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
                recyclerViewAdapterCatalog =
                    RecyclerViewAdapterCatalog(message,list, this@MyCatalog, R.layout.single_view,
                        arrayListOf())
                recyclerView.adapter = recyclerViewAdapterCatalog
                recyclerViewAdapterCatalog.notifyDataSetChanged()
                if(message.equals("employee")) {
                    var simpleCallback: ItemTouchHelper.SimpleCallback =
                        SwipeController(0, LEFT, this@MyCatalog)
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
            var clearList=ArrayList<Page>()
                for(item in list)
                {
                    var index=types.indexOf(item.type)
                    if(checkedTypes.get(index))
                        clearList.add(item)
                }
            recyclerViewAdapterCatalog=RecyclerViewAdapterCatalog(message,clearList,this@MyCatalog,R.layout.single_view,list)
            recyclerView.adapter=recyclerViewAdapterCatalog
            recyclerViewAdapterCatalog.notifyDataSetChanged()
        }
        builder.setNeutralButton(
            "Отмена"
        ) { dialog, which ->
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

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun swipe(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is RecyclerViewAdapterCatalog.MyViewHolder) {
                recyclerViewAdapterCatalog.removeItem(position)
        }
    }
}

