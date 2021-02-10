package dev.jamiecraane.betterrecyclerview.views

import android.content.Context
import android.util.AttributeSet
import dev.jamiecraane.betterrecyclerview.model.PersonModel
import dev.jamiecraane.betterrecyclerview.BetterRecyclerView
import dev.jamiecraane.betterrecyclerview.api.PersonApi
import nl.capaxambi.shared.extensions.dp
import nl.capaxambi.wordtranslator.androidapp.components.DragAndDropConfig
import nl.capaxambi.wordtranslator.androidapp.components.decorators.DecoratorSpec
import nl.capaxambi.wordtranslator.androidapp.components.decorators.TopSpaceItemDecorator

class PersonRecyclerView(context: Context, attributeSet: AttributeSet? = null) : BetterRecyclerView<PersonModel>(context, attributeSet) {
    init {
        dragAndDropConfig = DragAndDropConfig(
            dragUsingDragHandle = true,
            dragUsingLongPress = true
        )

        addItemDecoration(TopSpaceItemDecorator(6f.dp(context), DecoratorSpec.ODD_ITEMS))
    }
}
