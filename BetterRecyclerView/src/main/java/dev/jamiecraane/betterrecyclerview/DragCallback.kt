package dev.jamiecraane.betterrecyclerview

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Generic drag call back to handle drag and drop in the BetterRecyclerView.
 *
 * @property betterRecyclerView Reference to betterRecyclerview to let the view know about drag&drop operations.
 * @property movementFlags Defines which directions are supported when dragging. Defaults to UP and DOWN. Please note
 * that the movement flags can be overriden on a per ItemView basis. The movementFlags of the ItemView gets precedence if
 * they are specified.
 */
class DragCallback<T>(
    private val betterRecyclerView: BetterRecyclerView<T>,
    private val movementFlags: Set<Int> = setOf(ItemTouchHelper.UP, ItemTouchHelper.DOWN)
) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val itemView = viewHolder.itemView as? BetterRecyclerView.ItemView<*>
        val notDraggable = itemView?.isDraggable() == false
        val viewMovementFlags = itemView?.getMovementFlags() ?: emptySet()

//        todo use view movement flags.
        val dragFlags = if (movementFlags.isEmpty() || notDraggable) {
            0
        } else {
            var flags = movementFlags.first()
            movementFlags.drop(1).forEach {
                flags = flags or it
            }
            flags
        }

        return makeMovementFlags(dragFlags, 0)
    }

    override fun isLongPressDragEnabled() = betterRecyclerView.dragAndDropConfig?.dragUsingLongPress == true

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        betterRecyclerView.betterAdapter?.onMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Not supported
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        betterRecyclerView.betterAdapter?.notifyDataSetChanged()
    }
}