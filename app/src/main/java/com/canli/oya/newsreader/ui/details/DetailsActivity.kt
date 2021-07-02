package com.canli.oya.newsreader.ui.details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.canlioya.core.model.NewsArticle
import android.content.Intent
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.canli.oya.newsreader.R
import com.canli.oya.newsreader.common.*
import com.canli.oya.newsreader.data.Interactors
import com.canli.oya.newsreader.databinding.ActivityDetailsBinding
import com.canli.oya.newsreader.ui.bookmarks.BookmarkActivity
import com.canli.oya.newsreader.ui.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    lateinit var binding : ActivityDetailsBinding

    private val viewModel : DetailsViewModel by viewModels()

    private var chosenArticle: NewsArticle? = null

    @Inject
    lateinit var interactors: Interactors

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Set the toolbar and enable up button
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        chosenArticle = intent?.getSerializableExtra(CHOSEN_ARTICLE) as? NewsArticle
        if(chosenArticle != null) {
            populateUI(chosenArticle!!)
        } else {
            shortToast("Error retrieving chosen article.")
            onBackPressed()
        }

        if (savedInstanceState != null) {
            val x = savedInstanceState.getInt(SCROLL_X)
            val y = savedInstanceState.getInt(SCROLL_Y)
            binding.detailsRootScroll.post {
                binding.detailsRootScroll.scrollTo(x, y)
            }
        }

    }

    private fun populateUI(chosenArticle: NewsArticle) {
        with(binding){
            detailsTitle.text = chosenArticle.title
            detailsAuthor.text = getString(R.string.byline, chosenArticle.author)
            detailsTrail.text = fromHtml(chosenArticle.articleTrail)
            detailsBody.text = fromHtml(chosenArticle.articleBody)
            detailsBody.movementMethod = LinkMovementMethod.getInstance()
            detailsImage.bindImage(chosenArticle.thumbnailUrl)
            detailsSection.text = chosenArticle.section
            detailsTime.text = splitDateAndTime(chosenArticle.date)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_details, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val bookmarkItem = menu?.getItem(3)
        if(chosenArticle?.isBookmarked == true){
            bookmarkItem?.setIcon(R.drawable.ic_bookmark_filled)
        } else {
            bookmarkItem?.setIcon(R.drawable.ic_bookmark_outlined)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this@DetailsActivity, SettingsActivity::class.java)
                intent.putExtra(
                    USER_CLICKED_SETTINGS_FROM,
                    DetailsActivity::class.java.simpleName
                )
                startActivity(intent)
            }
            R.id.action_bookmark -> {
                viewModel.toggleBookmarkState(chosenArticle!!)
                chosenArticle?.isBookmarked = !chosenArticle!!.isBookmarked
                invalidateOptionsMenu()
            }
            R.id.action_share -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, chosenArticle?.webUrl)
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
            R.id.action_bookmarks -> {
                val intent = Intent(this@DetailsActivity, BookmarkActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCROLL_X, binding.detailsRootScroll.scrollX)
        outState.putInt(SCROLL_Y, binding.detailsRootScroll.scrollY)
    }
}