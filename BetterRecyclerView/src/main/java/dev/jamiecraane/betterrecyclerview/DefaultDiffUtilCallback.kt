package dev.jamiecraane.betterrecyclerview

import androidx.recyclerview.widget.DiffUtil

/**
 * @property oldList The original items in the recyclerview.
 * @property newList The new items to be added.
 * @property areItemsSame function to determine if two items have the same contents. Defaults to equality. If data classes
 * @property areContentsSame function to determine if two items have the same contents. Defaults to equality. If data classes
 * are used this function usually is not needed since data classes implement equals on the fields it defines.
 */
class DefaultDiffUtilCallback<T>(
    private val oldList: List<RecyclerItem<T>>,
    private val newList: List<RecyclerItem<T>>,
    private val areItemsSame: (oldItem: RecyclerItem<T>, newItem: RecyclerItem<T>) -> Boolean,
    private val areContentsSame: (oldItem: RecyclerItem<T>, newItem: RecyclerItem<T>) -> Boolean
) :
    DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        this.areItemsSame(oldList[oldItemPosition], newList[newItemPosition])

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        this.areContentsSame(oldList[oldItemPosition], newList[newItemPosition])
}