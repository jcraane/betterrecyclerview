package dev.jamiecraane.betterrecyclerview.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import dev.jamiecraane.betterrecyclerview.BetterRecyclerView
import dev.jamiecraane.betterrecyclerview.databinding.ViewHeaderBinding
import dev.jamiecraane.betterrecyclerview.model.PersonModel

class HeaderView(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet),
    BetterRecyclerView.ItemView<PersonModel> {
    private val binding = ViewHeaderBinding.inflate(LayoutInflater.from(context), this)

    init {
        setBackgroundColor(Color.CYAN)
    }

    override fun update(item: PersonModel, position: Int) {
        binding.name.text = item.name
    }

    override fun isDraggable() = false
}