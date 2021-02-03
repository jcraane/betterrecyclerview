package nl.capaxambi.wordtranslator.androidapp.components

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * @param dragUsingDragHandle If true and the ItemView has a dragHandle, dragging is started by pressing down on the draghandle and start dragging.
 * @param dragUsingLongPress If true, dragging is enabled by long pressing the ItemView.
 */
data class DragAndDropConfig(val dragUsingDragHandle: Boolean = false, val dragUsingLongPress: Boolean = false)

class DragListener(private val touchHelper: ItemTouchHelper) {
    fun requestDrag(viewHolder: RecyclerView.ViewHolder) {
        touchHelper.startDrag(viewHolder)
    }
}