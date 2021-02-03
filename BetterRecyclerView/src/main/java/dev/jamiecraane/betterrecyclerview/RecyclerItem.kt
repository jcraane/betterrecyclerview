package nl.capaxambi.shared.common


/**
 * Interface which the items of the recyclerview must implement.
 */
interface RecyclerItem {
    /**
     * The item type which is used to determine the viewType. Defaults to 0.
     */
    fun getType() = 0
}