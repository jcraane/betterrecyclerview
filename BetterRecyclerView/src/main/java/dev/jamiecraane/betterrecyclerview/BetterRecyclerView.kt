package nl.capaxambi.wordtranslator.androidapp.components

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.jamiecraane.betterrecyclerview.extensions.hasItemFor
import nl.capaxambi.shared.common.RecyclerItem
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
 * For every itemViewType there must be a corresponding function to create the view for this item type.
 *
 * Example:
 *
 * '''
betterRecyclerView.itemsAndBuilder = Pair(items, mapOf<Int, () -> View>(
0 to {PersonView(this)},
1 to {HeaderView(this)}
))
 * '''
 *
 * where persons is a list of Item objects.
 *
 * By default this class creates a LinearLayoutManager where its orientation is specified using the orientation
 * property. You can soecify a custom layout manager directly if desired.
 *
 * @author jcraane
 */
open class BetterRecyclerView(context: Context, attributeSet: AttributeSet? = null) :
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
    var betterAdapter: BetterRecyclerAdapter? = null
        private set

    var onItemClickListener: OnItemClickListener? = null
    var onEndlessScrollingListener: OnEndlessScrolling? = null
    var dragListener: DragListener? = null
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

    /**
     * The items to render together with the functions (mapped to viewType) to create the views for the items.
     */
    var itemsAndBuilder: Pair<MutableList<RecyclerItem>, Map<Int, () -> View>>? = null
        set(value) {
            field = value
            itemsAndBuilder?.let {
                betterAdapter =
                    BetterRecyclerAdapter(
                        it.first,
                        it.second,
                        onItemClickListener,
                        onEndlessScrollingListener,
                        dragListener
                    )
                adapter = betterAdapter
            }
        }

    init {
        layoutManager = getLayoutMananger(orientation)
    }

    /**
     * Convenient function which can be used if this recyvlerview only supports one view type.
     *
     * @param items The items to render
     * @param viewBuilder The function to create the View for the items
     */
    fun setItems(items: List<RecyclerItem>, viewBuilder: () -> View) {
        itemsAndBuilder = Pair(
            items.toMutableList(), mapOf(
                0 to viewBuilder
            )
        )
    }

    /**
     * Sets a mutable list. Useful if a list is mutated from outside this adapter. Please note that when
     * the list is changed, the corresponding update methods on the adapter needs to be called, for
     * example notifyItemAdded(position).
     */
    fun setMutableItems(items: MutableList<RecyclerItem>, viewBuilder: () -> View) {
        itemsAndBuilder = Pair(
            items, mapOf(
                0 to viewBuilder
            )
        )
    }

    fun addPreviousItems(items: List<RecyclerItem>, done: () -> Unit = {}) {
        items.reversed().forEach {
            betterAdapter?.addItem(it, 0)
        }
        done()
    }

    fun resetScrollOffsets() {
        scrollXOffset = 0
        scrollYOffset = 0
    }

    override fun onScrolled(dx: Int, dy: Int) {
        scrollXOffset += dx
        scrollYOffset += dy
    }

    fun addNextItems(items: List<RecyclerItem>, done: () -> Unit = {}) {
        items.forEach {
            betterAdapter?.addItem(it)
        }
        done()
    }

    fun findFirstCompletelyVisibleItem(condition: (RecyclerItem?) -> Boolean): RecyclerItem? {
        val firstPositionIndex = (layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition() ?: NO_POSITION
        return if (firstPositionIndex == NO_POSITION) {
            null
        } else {
            var found: RecyclerItem? = null
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

    fun findLastCompletelyVisibleItem(condition: (RecyclerItem?) -> Boolean): RecyclerItem? {
        val lastPositionIndex = (layoutManager as? LinearLayoutManager)?.findLastCompletelyVisibleItemPosition() ?: NO_POSITION
        return if (lastPositionIndex == NO_POSITION) {
            null
        } else {
            var found: RecyclerItem? = null
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
    fun notifyItemChanged(item: RecyclerItem) {
        adapter?.notifyItemChanged((adapter as BetterRecyclerAdapter).indexOfItem(item))
    }

    fun notifyDataSetChanged() {
        adapter?.notifyDataSetChanged()
    }

    class BetterViewHolder<ItemView>(view: View) : RecyclerView.ViewHolder(view) {
        @Suppress("UNCHECKED_CAST") // found not a better way yet
        val betterView: ItemView = view as ItemView
    }

    class BetterRecyclerAdapter(
        val items: MutableList<RecyclerItem>,
        private val viewBuilders: Map<Int, () -> View>,
        private val onItemClickListener: OnItemClickListener?,
        private val onEndlessScrollingListener: OnEndlessScrolling?,
        private val dragListener: DragListener? = null
    ) : RecyclerView.Adapter<BetterViewHolder<ItemView<RecyclerItem>>>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): BetterViewHolder<ItemView<RecyclerItem>> {
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
        fun indexOfItem(item: RecyclerItem): Int {
            return items.indexOf(item)
        }

        fun removeItem(position: Int) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }

        fun removeItem(item: RecyclerItem) {
            val index = items.indexOf(item)
            items.remove(item)
            notifyItemRemoved(index)
        }

        /**
         * Adds the item at the given index. At the end if the index is null
         */
        fun addItem(item: RecyclerItem, index: Int? = null) {
            val indexToAdd = index ?: items.size
            items.add(indexToAdd, item)
            notifyItemInserted(indexToAdd)
        }

        fun updateItem(item: RecyclerItem, position: Int) {
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
        fun updateItem(position: Int, item: RecyclerItem) {
            if (items.hasItemFor(position)) {
                items[position] = item
                notifyItemChanged(position)
            }
        }

        override fun onBindViewHolder(vh: BetterViewHolder<ItemView<RecyclerItem>>, position: Int) {
            val item = items[position]
            onItemClickListener?.let { listener ->
                (vh.betterView as View).setOnClickListener {
                    listener.onItemClicked(item, vh.betterView)
                }
            }
            if (position == 0) {
                onEndlessScrollingListener?.onLoadStart()
            } else if (position == items.size - 1) {
                onEndlessScrollingListener?.onLoadEnd()
            }
            if (vh.betterView.getDragConfig().dragUsingDragHandle) {
                vh.betterView.getDragHandle()?.setOnTouchListener { view, motionEvent ->
                    if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                        dragListener?.requestDrag(vh)
                    }
                    false
                }
            }
            vh.betterView.update(item, position)
        }

        override fun onViewDetachedFromWindow(holder: BetterViewHolder<ItemView<RecyclerItem>>) {
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

        fun getDragConfig() = DragAndDropConfig()
    }

    /**
     * (Optional) Simple interface to implement a simple click for an entire item. To implement click, or other
     * listeners on components within an item implement them in de update method of the ItemView.
     */
    interface OnItemClickListener {
        fun onItemClicked(item: RecyclerItem, view: View)
    }

    interface OnEndlessScrolling {
        fun onLoadStart()

        fun onLoadEnd()
    }
}
