package dev.jamiecraane.betterrecyclerview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jamiecraane.betterrecyclerview.api.PersonApi
import dev.jamiecraane.betterrecyclerview.domain.Person
import dev.jamiecraane.betterrecyclerview.model.PersonModel
import kotlinx.coroutines.launch

class PersonViewModel : ViewModel() {
    private val api = PersonApi()

    val persons = MutableLiveData<List<PersonModel>>()

    init {
        viewModelScope.launch {
            val items = listOf(PersonModel("All Persons", true))
            persons.value = items + api.loadInitial().map {PersonModel("${it.firstName} - ${it.lastName}")}
        }
    }
}