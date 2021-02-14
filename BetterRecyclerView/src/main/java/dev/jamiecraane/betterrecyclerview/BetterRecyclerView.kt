package dev.jamiecraane.betterrecyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.jamiecraane.betterrecyclerview.extensions.hasItemFor
import nl.capaxambi.wordtranslator.androidapp.components.DragAndDropConfig
import nl.capaxambi.wordtranslator.androidapp.components.DragListener
import java.util.*


/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * RecyclerView which simplifies the creation of a RecyclerView by abstracting away the burden to create an adapter and
 * viewholder classes. This class simply accepts one or more functions responsible for creating te views. Items of the recyclerview
 * must implement the Item interface and Views which are used in this recycler view must implement the ItemView<Item> interface.
 *
 * For every itemViewType there must be a corresponding function to create the view for this item type. A convenience configure
 * function exists for configuring the list of items and corresponding view mappings.
 *
 * Example:
 *
 * '''
betterRecyclerView..configure(persons) {
addMapping(PersonModel.REGULAR_ITEM to { PersonView(this@MainActivity) })
addMapping(PersonModel.HEADER_ITEM to { HeaderView(this@MainActivity) })
}
 * '''
 *
 * where persons is a list of Item objects.
 *
 * If only one type of view is required, setting the items and view builder using the setItems function is enough:
 * betterRecyclerView.setItems(persons) {
 *      0 to { PersonView(this@MainActivity) }
 * }
 *
 * By default this class creates a LinearLayoutManager where its orientation is specified using the orientation
 * property. You can soecify a custom layout manager directly if desired.
 *
 * This class is type safe, that is, the type of the items in the adapter is represented by type T. In order for
 * this to work in XML, a subclass must be defined with a concrete type. In XML it is not possible to specify this type
 * parameter.
 *
 * @author jcraane
 */
open class BetterRecyclerView<T>(context: Context, attributeSet: AttributeSet? = null) :
    RecyclerView(context, attributeSet) {
    /**
     * Convenience property to directly use a vertical or horizontal LinearLayoutManager.
     */
    var orientation: Int = LinearLayoutManager.VERTICAL
        set(value) {
            field = value
            layoutManager = getLayoutMananger(field)
        }

    /**
     * The adapter for this recyclerview. Can be used to update items directly using the adapter.
     */
    var betterAdapter: BetterRecyclerAdapter<T>? = null
        private set

    /**
     * Called when an item is clicked.
     */
    var onItemClickListener: ((RecyclerItem<T>, View) -> Unit)? = null

    /**
     * Callback which, for example, can be used to load more data when the start or end of the recyclerview is reached.
     */
//    var onEndlessScrollingListener: OnEndlessScrolling? = null
    var onScrollStartReached: (() -> Unit)? = null
    var onScrollEndReached: (() -> Unit)? = null
    private var dragCallback: DragCallback<T>? = null
    private var itemTouchHelper: ItemTouchHelper? = null
    private var dragListener: DragListener? = null

    /**
     * Defines if this recycler view supports drag and drop.
     */
    var dragAndDropConfig: DragAndDropConfig? = null
        set(value) {
            field = value
            val f = field
            if (f != null) {
                val dcb = DragCallback(this)
                dragCallback = dcb
                itemTouchHelper = ItemTouchHelper(dcb).also {
                    dragListener = DragListener(it)
                    it.attachToRecyclerView(this)
                }
            } else {
                itemTouchHelper?.attachToRecyclerView(null)
            }
        }

    /**
     * Set to true to disable vertical scrolling.
     */
    var disableVerticalScrolling = false

    var scrollXOffset: Int = 0
    private var scrollYOffset: Int = 0

    private fun getLayoutMananger(field: Int): LayoutManager {
        return object : LinearLayoutManager(context, field, false) {
            override fun canScrollVertically(): Boolean {
                return if (disableVerticalScrolling) {
                    false
                } else {
                    super.canScrollVertically()
                }
            }
        }
    }

//    todo see if we can rewrite this in a more convenient fashion
    /**
     * The items to render together with the functions (mapped to viewType) to create the views for the items.
     */
    internal var itemsAndBuilder: Pair<List<RecyclerItem<T>>, Map<Int, () -> View>>? = null
        set(value) {
            field = value
            itemsAndBuilder?.let {
                betterAdapter =
                    BetterRecyclerAdapter(
                        it.first.toMutableList(),
                        it.second,
                        onItemClickListener,
                        onScrollStartReached,
                        onScrollEndReached,
                        dragAndDropConfig,
                        dragListener
                    )
                adapter = betterAdapter
            }
        }

    init {
        layoutManager = getLayoutMananger(orientation)
    }

    /**
     * Convenient function which can be used if this recyclerview only supports one view type.
     *
     * @param items The items to render
     * @param viewBuilder The function to create the View for the items
     */
    fun setItems(items: List<RecyclerItem<T>>, viewBuilder: () -> View) {
        itemsAndBuilder = Pair(
            items.toMutableList(), mapOf(
                0 to viewBuilder
            )
        )
    }

    /**
     * Update the items in the adapter using DiffUtil based on the functions areItemsSame and areContentsSame.
     */
    fun updateData(
        newList: List<RecyclerItem<T>>,
        areItemsSame: (oldItem: RecyclerItem<T>, newItem: RecyclerItem<T>) -> Boolean = { old, new ->
            old == new
        },
        areContentsSame: (oldItem: RecyclerItem<T>, newItem: RecyclerItem<T>) -> Boolean = { old, new ->
            old == new
        }
    ) {

        betterAdapter?.let { adapter ->
            val oldList = adapter.items
            val diffResult = DiffUtil.calculateDiff(DefaultDiffUtilCallback(oldList, newList, areItemsSame, areContentsSame))
            oldList.clear()
            oldList.addAll(newList)
            diffResult.dispatchUpdatesTo(adapter)
        }
    }

    /**
     * Sets a mutable list. Useful if a list is mutated from outside this adapter. Please note that when
     * the list is changed, the corresponding update methods on the adapter needs to be called, for
     * example notifyItemAdded(position).
     */
    fun setMutableItems(items: MutableList<RecyclerItem<T>>, viewBuilder: () -> View) {
        itemsAndBuilder = Pair(
            items, mapOf(
                0 to viewBuilder
            )
        )
    }

    fun addPreviousItems(items: List<RecyclerItem<T>>, done: () -> Unit = {}) {
        post {
            items.reversed().forEach {
                betterAdapter?.addItem(it, 0)
            }
            done()
        }
    }

    fun resetScrollOffsets() {
        scrollXOffset = 0
        scrollYOffset = 0
    }

    override fun onScrolled(dx: Int, dy: Int) {
        scrollXOffset += dx
        scrollYOffset += dy
    }

    fun addNextItems(items: List<RecyclerItem<T>>, done: () -> Unit = {}) {
        post {
            items.forEach {
                betterAdapter?.addItem(it)
            }
            done()
        }
    }

    fun findFirstCompletelyVisibleItem(condition: (RecyclerItem<T>?) -> Boolean): RecyclerItem<T>? {
        val firstPositionIndex = (layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition() ?: NO_POSITION
        return if (firstPositionIndex == NO_POSITION) {
            null
        } else {
            var found: RecyclerItem<T>? = null
            var pos = firstPositionIndex
            if (betterAdapter?.items?.hasItemFor(pos) == true) {
                found = betterAdapter?.items?.get(pos)
            }
            while (!condition(found)) {
                pos++
                if (betterAdapter?.items?.hasItemFor(pos) == true) {
                    found = betterAdapter?.items?.get(pos)
                } else {
                    break
                }
            }

            found
        }
    }

    fun findLastCompletelyVisibleItem(condition: (RecyclerItem<T>?) -> Boolean): RecyclerItem<T>? {
        val lastPositionIndex = (layoutManager as? LinearLayoutManager)?.findLastCompletelyVisibleItemPosition() ?: NO_POSITION
        return if (lastPositionIndex == NO_POSITION) {
            null
        } else {
            var found: RecyclerItem<T>? = null
            var pos = lastPositionIndex
            if (betterAdapter?.items?.hasItemFor(pos) == true) {
                found = betterAdapter?.items?.get(pos)
            }
            while (!condition(found)) {
                pos--
                if (betterAdapter?.items?.hasItemFor(pos) == true) {
                    found = betterAdapter?.items?.get(pos)
                } else {
                    break
                }
            }

            found
        }
    }

    /**
     * Delegate the notifyItemChanged to the adapter with the position of the item
     *
     * @param item The item that has changed
     */
    fun notifyItemChanged(item: RecyclerItem<T>) {
        betterAdapter?.indexOfItem(item)?.let { index ->
            adapter?.notifyItemChanged(index)
        }
    }

    fun notifyDataSetChanged() {
        adapter?.notifyDataSetChanged()
    }

    class BetterViewHolder<ItemView>(view: View) : RecyclerView.ViewHolder(view) {
        @Suppress("UNCHECKED_CAST") // found not a better way yet
        val betterView: ItemView = view as ItemView
    }

    class BetterRecyclerAdapter<T>(
        val items: MutableList<RecyclerItem<T>>,
        private val viewBuilders: Map<Int, () -> View>,
        private val onItemClickListener: ((RecyclerItem<T>, View) -> Unit)? = null,
        private var onScrollStartReached: (() -> Unit)?,
        private var onScrollEndReached: (() -> Unit)?,
        private val dragAndDropConfig: DragAndDropConfig? = null,
        private val dragListener: DragListener?
    ) : RecyclerView.Adapter<BetterViewHolder<ItemView<RecyclerItem<T>>>>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): BetterViewHolder<ItemView<RecyclerItem<T>>> {
            val viewBuilder = viewBuilders[viewType]
            if (viewBuilder != null) {
                return BetterViewHolder(viewBuilder())
            } else {
                throw IllegalArgumentException("No viewBuilder found for itemViewType $viewType")
            }
        }

        override fun getItemViewType(position: Int): Int {
            return if (items.hasItemFor(position)) {
                items[position].getType()
            } else {
                0
            }
        }

        override fun getItemCount() = items.size

        /**
         * Returns the index of the Item in the list of Item. -1 if not present.
         */
        fun indexOfItem(item: RecyclerItem<T>): Int {
            return items.indexOf(item)
        }

        fun removeItem(position: Int) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }

        fun removeItem(item: RecyclerItem<T>) {
            val index = items.indexOf(item)
            items.remove(item)
            notifyItemRemoved(index)
        }

        /**
         * Adds the item at the given index. At the end if the index is null
         */
        fun addItem(item: RecyclerItem<T>, index: Int? = null) {
            val indexToAdd = index ?: items.size
            items.add(indexToAdd, item)
            notifyItemInserted(indexToAdd)
        }

        fun updateItem(item: RecyclerItem<T>, position: Int) {
            items[position] = item
            notifyItemChanged(position)
        }

        /**
         * Swap the item on position fromPosition to position toPosition
         *
         * @param fromPosition The original position of the item to be swapped.
         * @param toPosition The new position of the item which is swapped.
         */
        fun onMoved(fromPosition: Int, toPosition: Int) {
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(items, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(items, i, i - 1)
                }
            }
            notifyItemMoved(fromPosition, toPosition)
        }

        /**
         * Updates the item at position with a new item.
         *
         * @param position The position of the item to update.
         * @param item The new item to set at position.
         */
        fun updateItem(position: Int, item: RecyclerItem<T>) {
            if (items.hasItemFor(position)) {
                items[position] = item
                notifyItemChanged(position)
            }
        }

        override fun onBindViewHolder(vh: BetterViewHolder<ItemView<RecyclerItem<T>>>, position: Int) {
            val item = items[position]
            onItemClickListener?.let { listener ->
                (vh.betterView as View).setOnClickListener {
                    listener.invoke(item, vh.betterView)
                }
            }
            if (position == 0) {
                onScrollStartReached?.invoke()
            } else if (position == items.size - 1) {
                onScrollEndReached?.invoke()
            }
            if (dragAndDropConfig?.dragUsingDragHandle == true) {
                vh.betterView.getDragHandle()?.setOnTouchListener { _, motionEvent ->
                    if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                        dragListener?.requestDrag(vh)
                    }
                    false
                }
            }
            vh.betterView.update(item, position)
        }

        override fun onViewDetachedFromWindow(holder: BetterViewHolder<ItemView<RecyclerItem<T>>>) {
            super.onViewDetachedFromWindow(holder)
            holder.betterView.onDetached()
        }
    }

    /**
     * Interface which views of the recyclerview must implement.
     */
    interface ItemView<Item> {
        /**
         * Updates this view with the given item.
         */
        fun update(item: Item, position: Int)

        fun onDetached() {}

        /**
         * If getDragCaonfig.dragUsingDragHandle is true, this view (if not null) is used to start drag the item.
         */
        fun getDragHandle(): View? = null

        fun isDraggable(): Boolean = false

        /**
         * The DragCallback has default movement flags (which direction a view can be dragged to). With this method views can customize
         * the direction in which a view can be dragged on a pre-view basis. If empty the movement flags of DragCallback are used.
         */
        fun getMovementFlags(): Set<Int> = emptySet()
    }

    interface OnEndlessScrolling {
        fun onLoadStart()

        fun onLoadEnd()
    }

    class BetterRecyclerViewConfig<T>(val items: List<RecyclerItem<T>>) {
        val viewBuilders = mutableMapOf<Int, () -> View>()

        fun addMapping(mapping: Pair<Int, () -> View>) {
            viewBuilders[mapping.first] = mapping.second
        }
    }
}

fun <T> BetterRecyclerView<T>.configure(items: List<RecyclerItem<T>>, block: BetterRecyclerView.BetterRecyclerViewConfig<T>.() -> Unit) {
    val config = BetterRecyclerView.BetterRecyclerViewConfig(items).apply(block)
    itemsAndBuilder = config.items to config.viewBuilders
}