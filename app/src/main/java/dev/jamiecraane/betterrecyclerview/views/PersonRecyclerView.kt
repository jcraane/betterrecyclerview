package dev.jamiecraane.betterrecyclerview.views

import android.content.Context
import android.util.AttributeSet
import dev.jamiecraane.betterrecyclerview.model.PersonModel
import dev.jamiecraane.betterrecyclerview.BetterRecyclerView
import nl.capaxambi.wordtranslator.androidapp.components.DragAndDropConfig

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
