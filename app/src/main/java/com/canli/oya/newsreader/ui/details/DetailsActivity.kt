package com.canli.oya.newsreader.ui.details

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.canli.oya.newsreader.common.CHOSEN_ARTICLE
import com.canli.oya.newsreader.common.shortToast
import com.canli.oya.newsreader.data.Interactors
import com.canli.oya.newsreader.ui.main.MainScreen
import com.canlioya.core.model.NewsArticle
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailsActivity : ComponentActivity() {

    private val viewModel: DetailsViewModel by viewModels()

    @Inject
    lateinit var interactors: Interactors

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chosenArticle = intent?.getSerializableExtra(CHOSEN_ARTICLE) as? NewsArticle

        if (chosenArticle == null) {
            shortToast("Error retrieving chosen article.")
            onBackPressed()
        }

        setContent {
            var isBookmarked by remember {
                mutableStateOf(chosenArticle!!.isBookmarked)
            }

            MainScreen(
                topAppBar = {
                    DetailsAppBar(
                        url = chosenArticle!!.webUrl,
                        isBookmarked = isBookmarked,
                        onUpClicked = {
                            onBackPressed()
                        },
                        onBookmarkClicked = {
                            isBookmarked = !isBookmarked
                            viewModel.toggleBookmarkState(chosenArticle)
                        }
                    )
                },
                content = {
                    DetailsScreen(chosenArticle!!)
                }
            )
        }
    }
}
