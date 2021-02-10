package dev.jamiecraane.betterrecyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import dev.jamiecraane.betterrecyclerview.databinding.ActivityMainBinding
import dev.jamiecraane.betterrecyclerview.model.PersonModel
import dev.jamiecraane.betterrecyclerview.views.HeaderView
import dev.jamiecraane.betterrecyclerview.views.PersonView

class MainActivity : AppCompatActivity() {
    private val viewModel: PersonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.persons.observe(this) {
            binding.personsRecyclerView.configure(it) {
                addMapping(PersonModel.REGULAR_ITEM to { PersonView(this@MainActivity) })
                addMapping(PersonModel.HEADER_ITEM to { HeaderView(this@MainActivity) })
            }

//            binding.personsRecyclerView.persons = it
        }

        binding.personsRecyclerView.onItemClickListener = { recyclerItem, view ->
            Toast.makeText(this, "Clicked on ${recyclerItem.data.name}", Toast.LENGTH_SHORT).show()
        }

//        todo add endless scrolling
//        todo add multiple views
    }
}
