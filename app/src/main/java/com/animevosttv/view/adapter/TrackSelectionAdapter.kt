package com.animevosttv.view.adapter

import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.animevosttv.R


/**
 * Taken from http://goo.gl/vkm1b1 and slightly modified this provides us with an Adapter which holds
 * the currently selected item. This can also easily be transformed to multi-selection.
 *
 * @param <VH> The ViewHolder which needs to be extending
 * [com.supenta.flitchio.taskerplugin.lists.TrackSelectionAdapter.ViewHolder]
</VH> */
abstract class TrackSelectionAdapter<VH> :
    RecyclerView.Adapter<VH>() where VH : TrackSelectionAdapter.ViewHolder {
    private var selectedItem = -1

    /**
     * This provides keyboard support.
     *
     * @param recyclerView
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        // Handle key up and key down and attempt to move selection
        recyclerView.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            val lm = recyclerView.layoutManager

            // Return false if scrolled to the bounds and allow focus to move off the list
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    return@OnKeyListener tryMoveSelection(lm, 1)
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    return@OnKeyListener tryMoveSelection(lm, -1)
                }
            }
            false
        })
    }

    private fun tryMoveSelection(lm: RecyclerView.LayoutManager?, direction: Int): Boolean {
        val nextSelectItem = selectedItem + direction

        // If still within valid bounds, move the selection, notify to redraw, and scroll
        if (nextSelectItem in 0 until itemCount) {
            changeSelectionAndNotify(nextSelectItem)
            lm!!.scrollToPosition(selectedItem)
            return true
        }
        return false
    }

    private fun changeSelectionAndNotify(nextSelectedItem: Int) {
//        Timber.d("Previous selection: %d. Next selection: %d ", selectedItem, nextSelectedItem);
        notifyItemChanged(selectedItem)
        selectedItem = nextSelectedItem
        notifyItemChanged(selectedItem)
    }

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        // Set selected state; use a state list drawable to style the view
        viewHolder.itemView.isSelected = selectedItem == position
        viewHolder.itemView.setOnClickListener { // Redraw the old selection and the new
            changeSelectionAndNotify(position)
        }
    }

    abstract class ViewHolder(itemView: View, animate: Boolean) :
        RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnFocusChangeListener { v, hasFocus ->
                if (!animate)
                    return@setOnFocusChangeListener

                if (hasFocus) {
                    // run scale animation and make it bigger
                    val anim: Animation =
                        AnimationUtils.loadAnimation(itemView.context, R.anim.scale_in_tv)
                    itemView.startAnimation(anim)
                    anim.fillAfter = true
                    onFocus()
                } else {
                    // run scale animation and make it smaller
                    val anim: Animation =
                        AnimationUtils.loadAnimation(itemView.context, R.anim.scale_out_tv)
                    itemView.startAnimation(anim)
                    anim.fillAfter = true
                    onUnFocus()
                }
            }

        }

        open fun onFocus() {}
        open fun onUnFocus() {}
    }
}