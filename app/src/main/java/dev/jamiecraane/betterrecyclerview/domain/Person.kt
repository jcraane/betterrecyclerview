package dev.jamiecraane.betterrecyclerview.domain

import dev.jamiecraane.betterrecyclerview.RecyclerItem

data class Person(
    val firstName: String,
    val lastName: String
) : RecyclerItem<Person> {
    override val data: Person
        get() = this
}
