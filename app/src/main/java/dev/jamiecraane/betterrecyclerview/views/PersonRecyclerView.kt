package dev.jamiecraane.betterrecyclerview.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.ItemTouchHelper
import dev.jamiecraane.betterrecyclerview.DragCallback
import dev.jamiecraane.betterrecyclerview.databinding.ViewHeaderBinding
import dev.jamiecraane.betterrecyclerview.databinding.ViewPersonsBinding
import dev.jamiecraane.betterrecyclerview.domain.Person
import dev.jamiecraane.betterrecyclerview.model.PersonModel
import nl.capaxambi.wordtranslator.androidapp.components.BetterRecyclerView
import nl.capaxambi.wordtranslator.androidapp.components.DragAndDropConfig
import nl.capaxambi.wordtranslator.androidapp.components.DragListener

class PersonRecyclerView(context: Context, attributeSet: AttributeSet? = null) : BetterRecyclerView<PersonModel>(context, attributeSet) {
    var persons: List<PersonModel> = emptyList()
        set(value) {
            field = value
            setItems(persons) { PersonView(context) }
        }

    init {
        dragAndDropConfig = DragAndDropConfig(
            dragUsingDragHandle = true,
            dragUsingLongPress = true
        )
    }
}

class PersonView(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet),
    BetterRecyclerView.ItemView<PersonModel> {

    private val binding = ViewPersonsBinding.inflate(LayoutInflater.from(context), this)

    init {
        orientation = HORIZONTAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        setPadding(16, 16, 16, 16)
    }

    override fun update(item: PersonModel, position: Int) {
        binding.name.text = item.name
    }

    override fun getDragHandle() = binding.dragHandle
}

class HeaderView(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet),
    BetterRecyclerView.ItemView<PersonModel> {
    private val binding = ViewHeaderBinding.inflate(LayoutInflater.from(context), this)

    init {
        setBackgroundColor(Color.CYAN)
    }

    override fun update(item: PersonModel, position: Int) {
        binding.name.text = item.name
    }
}
