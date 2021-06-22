package com.example.oya.newsreader.ui.details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.canlioya.core.model.NewsArticle
import com.example.oya.newsreader.databinding.ActivityDetailsBinding
import com.example.oya.newsreader.ui.newslist.CHOSEN_ARTICLE
import android.content.Intent
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import com.example.oya.newsreader.R
import com.example.oya.newsreader.common.*
import com.example.oya.newsreader.ui.settings.SettingsActivity


class DetailsActivity : AppCompatActivity() {

    lateinit var binding : ActivityDetailsBinding

    private var chosenArticle: NewsArticle? = null

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
            val x = savedInstanceState.getInt(Constants.SCROLL_X)
            val y = savedInstanceState.getInt(Constants.SCROLL_Y)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this@DetailsActivity, SettingsActivity::class.java)
                intent.putExtra(
                    Constants.USER_CLICKED_SETTINGS_FROM,
                    DetailsActivity::class.java.simpleName
                )
                startActivity(intent)
            }
            R.id.action_bookmark -> {
                //saveToBookmarks()
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
                /*val intent = Intent(this@DetailsActivity, BookmarksActivity::class.java)
                startActivity(intent)*/
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(Constants.SCROLL_X, binding.detailsRootScroll.scrollX)
        outState.putInt(Constants.SCROLL_Y, binding.detailsRootScroll.scrollY)
    }
}