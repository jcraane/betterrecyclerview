package dev.jamiecraane.betterrecyclerview.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import dev.jamiecraane.betterrecyclerview.BetterRecyclerView
import dev.jamiecraane.betterrecyclerview.databinding.ViewPersonsBinding
import dev.jamiecraane.betterrecyclerview.model.PersonModel

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

    override fun isDraggable() = true
}