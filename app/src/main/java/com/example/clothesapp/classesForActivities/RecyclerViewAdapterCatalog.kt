package com.example.clothesapp.classesForActivities

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothesapp.R
import com.example.clothesapp.activities.MainActivity
import com.example.clothesapp.db.MyDbManager
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class RecyclerViewAdapterCatalog(
    _typeOfUser: String,
    _arr: ArrayList<Page>,
    _context: Context,
    _layout: Int,
    _arr2: ArrayList<Page>
) : RecyclerView.Adapter<RecyclerViewAdapterCatalog.MyViewHolder>() {

    private var arr = _arr
    private var arr2 = _arr2
    private var context = _context
    private var layout = _layout
    private var databaseReference=FirebaseDatabase.getInstance().getReference("EDMT_FIREBASE")
    private lateinit var storageReference: StorageReference
    private var typeOfUser = _typeOfUser
    private var time: Long = 0
    private var isFirstTime = true
    private lateinit var viewToHolder: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        viewToHolder = view
        return MyViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (typeOfUser.equals("user")) {
            viewToHolder.setOnClickListener {
                holder.setIsRecyclable(false)
                if (isFirstTime) {
                    time = System.currentTimeMillis()
                    isFirstTime = false
                } else {
                    val manager = MyDbManager(context)
                    manager.openDb()
                    if (time + 500 > System.currentTimeMillis()) {
                        Toast.makeText(context, "?????????????????? ?? ??????????????????????????", Toast.LENGTH_SHORT).show()
                        val path = arr[position].path!!
                        val comment = arr[position].comment!!
                        var checker = true
                        val listOfPages = manager.readDbData()
                        for (i in listOfPages) {
                            if (i.path.equals(path))
                                checker = false
                        }
                        if (checker) {
                            manager.insertToDb(path, comment)
                            databaseReference.child(User.tableName).push().setValue(Page(path,"",comment)).addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "???????????????? ?? ?????????????? ?? ???????? ????????????.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                            }
                        }
                    }
                    time = System.currentTimeMillis()
                    manager.closeDb()
                }
            }
        }
        holder.textView?.text = arr[position].comment
        storageReference =
            FirebaseStorage.getInstance().getReferenceFromUrl("gs://myproject-df142.appspot.com")
        storageReference.child(arr[position].path!!)
            .downloadUrl
            .addOnSuccessListener { Uri ->
                holder.imageView?.let { Glide.with(context).load(Uri.toString()).into(it) }
            }
            .addOnFailureListener { }
    }

    fun removeItem(position: Int) {
        val path = arr[position].path!!
        arr.removeAt(position)
        notifyItemRemoved(position)
        if (typeOfUser.equals("employee")) {
            val query = databaseReference.child("photos")
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        context,
                        "???????????????? ?? ???????????????????????? ?? ???????? ???????????? ?????? ????????????.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        if (ds.getValue(Page::class.java)!!.path.equals(path)) {
                            ds.ref.removeValue()
                            break
                        }
                    }
                }
            })
            if (arr2.size != 0) {
                var counter = -1
                var checker = false
                for (i in arr2) {
                    counter++
                    if (i.path.equals(path)) {
                        checker = true
                        break
                    }
                }
                if (checker)
                    arr2.removeAt(counter)
            }
        } else {
            val manager = MyDbManager(context)
            manager.openDb()
            manager.deleteString(path)
            val query = databaseReference.child(User.tableName)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        context,
                        "???????????????? ?? ???????????????????????? ?? ???????? ???????????? ?????? ????????????.",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        val page=ds.getValue(Page::class.java)!!
                        if(page.path!!.equals(path)) {
                            ds.ref.removeValue()
                            break
                        }
                    }
                }
            })
            manager.closeDb()
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView? = null
        var textView: TextView? = null

        init {
            imageView = itemView.findViewById(R.id.imageInCatalog)
            textView = itemView.findViewById(R.id.commentInCatalog)
        }
    }
}