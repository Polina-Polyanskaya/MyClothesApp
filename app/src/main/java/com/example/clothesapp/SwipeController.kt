package com.example.clothesapp

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeController(dragDirs: Int, swipeDirs: Int, _listener:SwipeControllerListener) : ItemTouchHelper.SimpleCallback(
    dragDirs,
    swipeDirs
)
{
    private lateinit var listener: SwipeControllerListener

    init{
        listener=_listener
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.swipe(viewHolder,direction,viewHolder.adapterPosition)
    }

    interface SwipeControllerListener {
        fun swipe(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int)
    }

}