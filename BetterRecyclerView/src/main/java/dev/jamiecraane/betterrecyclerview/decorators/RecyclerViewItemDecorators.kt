package nl.capaxambi.wordtranslator.androidapp.components.decorators

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by jamiecraane on 16/11/2016.
 */

class TopSpaceItemDecorator(private val topSpace: Int = 0, private val spec: DecoratorSpec = DecoratorSpec.ALL_ITEMS) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        if (spec.matches(itemPosition, parent.childCount)) {
            outRect.top = topSpace
        }
    }
}

/**
 * Specification indicating to which items to apply the itemdecoration.
 */
enum class DecoratorSpec {
    FIRST_ITEM {
        override fun matches(itemPosition: Int, itemCount: Int) = itemPosition == 0
    },
    LAST_ITEM {
        override fun matches(itemPosition: Int, itemCount: Int) = itemPosition == itemCount - 1
    },
    ALL_ITEMS {
        override fun matches(itemPosition: Int, itemCount: Int) = true
    },
    EVEN_ITEMS {
        override fun matches(itemPosition: Int, itemCount: Int) = isEven(itemPosition)
    },
    ODD_ITEMS {
        override fun matches(itemPosition: Int, itemCount: Int) = isOdd(itemPosition)
    };

    abstract fun matches(itemPosition: Int, itemCount: Int): Boolean

    companion object {
        private fun isEven(itemPosition: Int) = itemPosition % 2 == 0
        private fun isOdd(itemPosition: Int) = !isEven(itemPosition)
    }
}


