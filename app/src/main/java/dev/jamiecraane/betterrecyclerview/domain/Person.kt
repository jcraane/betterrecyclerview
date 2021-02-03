package dev.jamiecraane.betterrecyclerview.domain

import nl.capaxambi.shared.common.RecyclerItem

data class Person(
    val firstName: String,
    val lastName: String
) : RecyclerItem
