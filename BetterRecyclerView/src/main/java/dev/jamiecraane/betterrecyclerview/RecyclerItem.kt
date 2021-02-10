package nl.capaxambi.shared.common


/**
 * Interface which the items of the recyclerview must implement.
 */
interface RecyclerItem<T> {
    val data: T
    /**
     * The item type which is used to determine the viewType. Defaults to 0.
     */
    fun getType() = 0
}