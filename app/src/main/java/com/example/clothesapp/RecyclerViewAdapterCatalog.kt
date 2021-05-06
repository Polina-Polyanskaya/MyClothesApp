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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.bumptech.glide.Glide
import com.example.clothesapp.db.myDbManager
import com.google.firebase.database.*

class RecyclerViewAdapterCatalog:RecyclerView.Adapter<RecyclerViewAdapterCatalog.MyViewHolder> {

    var arr=ArrayList<Page>()
    lateinit var context:Context
    var view:Int=0
    private lateinit var databaseReference: DatabaseReference
    private var storageReference: StorageReference?=null

    constructor() {}
    constructor(
        _arr: ArrayList<Page>,
        _context: Context,
        _view: Int

    ) {
        arr = _arr
        context = _context
        view = _view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder{
        var view=LayoutInflater.from(parent.context).inflate(view,parent,false)
        var myViewHolder=MyViewHolder(view)
        return myViewHolder
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
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
        databaseReference = FirebaseDatabase.getInstance().getReference("EDMT_FIREBASE")
        var query=databaseReference.child("photos")
        query.addListenerForSingleValueEvent(object : ValueEventListener
        {
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
                    var page=ds.getValue(Page::class.java)!!
                    if (page.path.equals(path)) {
                        ds.ref.removeValue()
                        break
                    }
                }
            }
        })
    }

    fun likeItem(position: Int)
    {
        var manager=myDbManager(context)
        var checker=true
        val path=arr.get(position).path!!
        val comment=arr.get(position).comment!!
        for (i in manager.readDbData())
        {
            if (i.path.equals(path))
                checker = false
        }
        if (checker)
            manager.insertToDb(path, comment)
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



