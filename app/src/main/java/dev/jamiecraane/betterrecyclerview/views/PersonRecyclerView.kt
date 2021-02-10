package dev.jamiecraane.betterrecyclerview.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.ItemTouchHelper
import dev.jamiecraane.betterrecyclerview.DragCallback
import dev.jamiecraane.betterrecyclerview.databinding.ViewPersonsBinding
import dev.jamiecraane.betterrecyclerview.domain.Person
import nl.capaxambi.wordtranslator.androidapp.components.BetterRecyclerView
import nl.capaxambi.wordtranslator.androidapp.components.DragAndDropConfig
import nl.capaxambi.wordtranslator.androidapp.components.DragListener

class PersonRecyclerView(context: Context, attributeSet: AttributeSet? = null) : BetterRecyclerView<Person>(context, attributeSet) {
    var persons: List<Person> = emptyList()
        set(value) {
            field = value
            setItems(persons) { PersonView(context) }
        }

    init {
        val callback = DragCallback(this)
        val touchHelper = ItemTouchHelper(callback)
        dragListener = DragListener(touchHelper)
        dragAndDropConfig = DragAndDropConfig(dragUsingDragHandle = true, dragUsingLongPress = false)
        touchHelper.attachToRecyclerView(this)
    }
}


class PersonView(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet),
    BetterRecyclerView.ItemView<Person> {

    private val binding = ViewPersonsBinding.inflate(LayoutInflater.from(context), this)

    init {
        orientation = HORIZONTAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        setPadding(16, 16, 16, 16)
    }

    override fun update(item: Person, position: Int) {
        binding.firstName.text = item.firstName
        binding.lastName.text = item.lastName
    }

    override fun getDragHandle() = binding.dragHandle
}