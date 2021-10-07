package com.canli.oya.newsreader.ui.settings

import android.graphics.Color
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.RecyclerView

class SectionMoveCallback(private val adapter : SortSectionsAdapter) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        // Notify your adapter that an item is moved from x position to y position
        adapter.swapItems(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
        adapter.notifyItemMoved(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun isLongPressDragEnabled() = true

    override fun isItemViewSwipeEnabled() = false

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
       if(actionState == ACTION_STATE_DRAG){
           (viewHolder as? SortSectionsAdapter.ViewHolder)?.binding?.itemSectionRoot?.setBackgroundColor(Color.LTGRAY)
       }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        (viewHolder as? SortSectionsAdapter.ViewHolder)?.binding?.itemSectionRoot?.setBackgroundColor(Color.WHITE)
    }
}