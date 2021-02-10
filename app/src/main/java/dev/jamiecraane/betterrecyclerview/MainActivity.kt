package dev.jamiecraane.betterrecyclerview

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import dev.jamiecraane.betterrecyclerview.databinding.ActivityMainBinding
import dev.jamiecraane.betterrecyclerview.databinding.ViewPersonsBinding
import dev.jamiecraane.betterrecyclerview.domain.Person
import dev.jamiecraane.betterrecyclerview.model.PersonModel
import dev.jamiecraane.betterrecyclerview.views.HeaderView
import dev.jamiecraane.betterrecyclerview.views.PersonView
import nl.capaxambi.wordtranslator.androidapp.components.BetterRecyclerView

class MainActivity : AppCompatActivity() {
    private val viewModel: PersonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        todo use builder function here.
        viewModel.persons.observe(this) {
            binding.personsRecyclerView.itemsAndBuilder = it to mapOf(
                PersonModel.REGULAR_ITEM to { PersonView(this) },
                PersonModel.HEADER_ITEM to { HeaderView(this) }
            )
//            binding.personsRecyclerView.persons = it
        }

        binding.personsRecyclerView.onItemClickListener = { recyclerItem, view ->
            Toast.makeText(this, "Clicked on ${recyclerItem.data.name}", Toast.LENGTH_SHORT).show()
        }

//        todo add endless scrolling
//        todo add multiple views
    }
}
