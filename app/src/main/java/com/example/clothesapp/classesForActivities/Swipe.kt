package com.example.clothesapp.classesForActivities

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class Swipe(dragDirs: Int, swipeDirs: Int, _listener: SwipeControllerListener) :
    ItemTouchHelper.SimpleCallback(
        dragDirs,
        swipeDirs
    ) {

    private var listener: SwipeControllerListener = _listener

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.swipe(viewHolder, direction, viewHolder.adapterPosition)
    }

    interface SwipeControllerListener {
        fun swipe(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int)
    }
}