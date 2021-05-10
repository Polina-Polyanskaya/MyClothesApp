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
import com.example.clothesapp.classesForActivities.InfoDialog
import com.example.clothesapp.classesForActivities.Page
import com.example.clothesapp.classesForActivities.RecyclerViewAdapterCatalog
import com.example.clothesapp.classesForActivities.Swipe
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_my_catalog)
        supportActionBar?.hide()
        recyclerView = findViewById(R.id.recyclerViewInCatalog)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        plusButton = findViewById(R.id.plus)
        infoButton = findViewById(R.id.infoButton)
        likedClothes = findViewById(R.id.LikedClothes)
        val arguments = intent.extras
        message = arguments?.get("message").toString()
        if (message.equals("user"))
            plusButton.visibility = View.GONE
        else
            likedClothes.visibility = View.GONE
        menuButton = findViewById(R.id.imageButton)
        trashCanButton = findViewById(R.id.trashCanButton)
        databaseReference = FirebaseDatabase.getInstance().getReference("EDMT_FIREBASE")
        val query = databaseReference.child("photos")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MyCatalog, "Проблемы с подключением к базе данных при чтении.", Toast.LENGTH_SHORT).show()
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
            "Футболка",
            "Платье",
            "Кофта",
            "Штаны",
            "Верхняя одежда",
            "Обувь"
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
        builder.setTitle("Выберите категории для поиска.")
        builder.setPositiveButton(
            "Поиск"
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
            "Отмена"
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
        builder.setTitle("Вы точно хотите выйти?")
        builder.setPositiveButton(
            "Да"
        ) { _, _ ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        builder.setNeutralButton(
            "Нет"
        ) { _, _ ->
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun swipe(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is RecyclerViewAdapterCatalog.MyViewHolder) {
                recyclerViewAdapterCatalog.removeItem(position)
        }
    }
}

