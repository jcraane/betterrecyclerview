package dev.jamiecraane.betterrecyclerview.api

import dev.jamiecraane.betterrecyclerview.domain.Person
import kotlinx.coroutines.delay

class PersonApi {
    private var number = 0

    fun loadInitial(): List<Person> {
        return (0..25).toList().map {
            number++
            Person("First_$number", "Last_$number")
        }
    }

    suspend fun loadMore(): List<Person> {
        delay(1000)
        return (0..5).toList().map {
            number++
            Person("First_$number", "Last_$number")
        }
    }
}