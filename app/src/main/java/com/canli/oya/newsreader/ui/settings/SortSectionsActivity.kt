package com.canli.oya.newsreader.ui.settings

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import com.canli.oya.newsreader.R
import com.canli.oya.newsreader.databinding.ActivitySortSectionsBinding
import com.canlioya.data.IUserPreferences
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class SortSectionsActivity : AppCompatActivity() {

    private lateinit var sectionsAdapter: SortSectionsAdapter

    @Inject
    lateinit var userPreferences : IUserPreferences

    lateinit var binding : ActivitySortSectionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySortSectionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.d("onCreate of SortSectionActivity")

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sections = userPreferences.getSectionListPreference()
        Timber.d("sections size : ${sections.size}")

        sectionsAdapter = SortSectionsAdapter(ArrayList(sections))

        val callback: ItemTouchHelper.Callback = SectionMoveCallback(sectionsAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.list)

        binding.list.adapter = sectionsAdapter
    }

    override fun onStop() {
        super.onStop()
        userPreferences.setSectionListPreference(sectionsAdapter.sectionList)
    }
}