package dev.jamiecraane.betterrecyclerview.model

import nl.capaxambi.shared.common.RecyclerItem

class PersonModel(
    val name: String,
    val header: Boolean = false
) : RecyclerItem<PersonModel> {

    override val data: PersonModel
        get() = this

    override fun getType() = if (header) HEADER_ITEM else REGULAR_ITEM

    companion object {
        const val REGULAR_ITEM = 0
        const val HEADER_ITEM = 1
    }
}
