package dev.jamiecraane.betterrecyclerview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jamiecraane.betterrecyclerview.api.PersonApi
import dev.jamiecraane.betterrecyclerview.domain.Person
import kotlinx.coroutines.launch

class PersonViewModel : ViewModel() {
    private val api = PersonApi()

    val persons = MutableLiveData<List<Person>>()

    init {
        viewModelScope.launch {
            persons.value = api.loadInitial()
        }
    }
}