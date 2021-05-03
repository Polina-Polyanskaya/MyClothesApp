package com.example.clothesapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.bumptech.glide.Glide

class RecyclerViewAdapter:RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    var arr=ArrayList<Page>()
    lateinit var context:Context

    constructor() {}
    constructor(
        _arr:ArrayList<Page>,
        _context:Context

    ) {
        arr = _arr
        context = _context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder{
        var view=LayoutInflater.from(parent.context).inflate(R.layout.single_view,parent,false)
        var myViewHolder=MyViewHolder(view)
        return myViewHolder
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView?.setText(arr.get(position).comment)
        var imageref = arr.get(position).path?.let {
            FirebaseStorage.getInstance().reference?.child(
                it
            )
        }
        imageref?.downloadUrl?.addOnSuccessListener {Uri->
            val imageURL = Uri.toString()
            holder?.imageView?.let {
                Glide.with(context)
                    .load(imageURL)
                    .into(it)
            }
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView?=null
        var textView: TextView?=null
        init{
            imageView= itemView?.findViewById(R.id.imageInCatalog)
            textView=itemView?.findViewById(R.id.typeInCatalog)
        }

    }

}



