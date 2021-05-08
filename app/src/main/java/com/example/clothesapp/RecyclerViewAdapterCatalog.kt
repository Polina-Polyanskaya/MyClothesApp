package com.example.clothesapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothesapp.db.myDbManager
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class RecyclerViewAdapterCatalog:RecyclerView.Adapter<RecyclerViewAdapterCatalog.MyViewHolder> {

    var arr=ArrayList<Page>()
    var arr_2=ArrayList<Page>()
    lateinit var context:Context
    var view: Int = 0
    private lateinit var databaseReference: DatabaseReference
    private var storageReference: StorageReference? = null
    private var typeOfUser = ""

    constructor() {}
    constructor(
        _typeOfUser:String,
        _arr: ArrayList<Page>,
        _context: Context,
        _view: Int,
        _arr2: ArrayList<Page>

    ) {
        typeOfUser = _typeOfUser
        arr = _arr
        context = _context
        view = _view
        arr_2 = _arr2
    }

    private var time: Long = 0
    private var isFirstTime=true
    private lateinit var viewToHolder: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder{
        var view=LayoutInflater.from(parent.context).inflate(view,parent,false)
        viewToHolder=view
        var myViewHolder=MyViewHolder(view)
        return myViewHolder
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(typeOfUser.equals("user")) {
            viewToHolder.setOnClickListener {
                if (isFirstTime) {
                    time = System.currentTimeMillis()
                    isFirstTime = false
                } else {
                    if (time + 500 > System.currentTimeMillis()) {
                        Toast.makeText(context, "Добавлено в понравившиеся", Toast.LENGTH_SHORT).show()
                        val path = arr.get(position).path!!
                        val comment = arr.get(position).comment!!
                        var manager = myDbManager(context)
                        manager.openDb()
                        var checker = true
                        for (i in manager.readDbData()) {
                            if (i.path.equals(path))
                                checker = false
                        }
                        if (checker) {
                            manager.insertToDb(path, comment)
                        }
                    }
                    time = System.currentTimeMillis()
                }
            }
        }
        holder.textView?.setText(arr.get(position).comment)
        storageReference =
            FirebaseStorage.getInstance().getReferenceFromUrl("gs://myproject-df142.appspot.com")
        storageReference?.child(arr.get(position).path!!)
            ?.downloadUrl
            ?.addOnSuccessListener { Uri ->
                holder?.imageView?.let { Glide.with(context).load(Uri.toString()).into(it) }
            }
            ?.addOnFailureListener {
            }
    }

    fun removeItem(position: Int)
    {
        val path=arr.get(position).path!!
        val comment=arr.get(position).comment!!
        arr.removeAt(position)
        notifyItemRemoved(position)
        if(typeOfUser.equals("employee")) {
            databaseReference = FirebaseDatabase.getInstance().getReference("EDMT_FIREBASE")
            var query = databaseReference.child("photos")
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    val text = "Проблемы с подключением к базе данных при чтении."
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(context, text, duration)
                    toast.show()
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        var page = ds.getValue(Page::class.java)!!
                        if (page.path.equals(path)) {
                            ds.ref.removeValue()
                            break
                        }
                    }
                }
            })
            if(arr_2.size!=0) {
                var counter = -1
                var checker = false
                for (i in arr_2) {
                    counter++
                    if (i.path.equals(path)) {
                        checker = true
                        break
                    }
                }
                if (checker)
                    arr_2.removeAt(counter)
            }

        } else {
            var manager = myDbManager(context)
            manager.openDb()
            manager.deleteString(path)
            manager.closeDb()
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView?=null
        var textView: TextView?=null
        init{
            imageView= itemView?.findViewById(R.id.imageInCatalog)
            textView=itemView?.findViewById(R.id.commentInCatalog)
        }
    }
}



