package dev.jamiecraane.betterrecyclerview

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.activity.viewModels
import dev.jamiecraane.betterrecyclerview.databinding.ActivityMainBinding
import dev.jamiecraane.betterrecyclerview.databinding.ViewPersonsBinding
import dev.jamiecraane.betterrecyclerview.domain.Person
import nl.capaxambi.wordtranslator.androidapp.components.BetterRecyclerView

class MainActivity : AppCompatActivity() {
    private val viewModel: PersonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.persons.observe(this) {
            binding.personsRecyclerView.persons = it
        }
    }
}

class PersonRecyclerView(context: Context, attributeSet: AttributeSet? = null) : BetterRecyclerView<Person>(context, attributeSet) {
    var persons: List<Person> = emptyList()
        set(value) {
            field = value
            setItems(persons) { PersonView(context) }
        }
}

class PersonView(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet),
    BetterRecyclerView.ItemView<Person> {

    private val binding = ViewPersonsBinding.inflate(LayoutInflater.from(context), this)

    init {
        orientation = HORIZONTAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        setPadding(16, 16, 16, 16)
    }

    override fun update(item: Person, position: Int) {
        binding.firstName.text = item.firstName
        binding.lastName.text = item.lastName
    }
}