package com.canli.oya.newsreader.ui.newslist

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.canli.oya.newsreader.R
import com.canli.oya.newsreader.common.UIState
import com.canli.oya.newsreader.common.fromHtml
import com.canli.oya.newsreader.common.shareTheLink
import com.canli.oya.newsreader.common.splitDateAndTime
import com.canli.oya.newsreader.ui.details.DetailsActivity
import com.canlioya.core.model.NewsArticle
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.coroutines.flow.StateFlow

@Composable
fun NewsListScreen(
    uiState: StateFlow<UIState>,
    list: StateFlow<List<NewsArticle>>,
    bookmarkClickListener: BookmarkClickListener
) {
    val articles by list.collectAsState()

    val state by uiState.collectAsState()

    when (state) {
        UIState.LOADING -> LoadingIndicator()
        UIState.SUCCESS -> NewsList(list = articles, bookmarkClickListener)
        UIState.EMPTY -> EmptyScreen()
        UIState.ERROR -> {/*todo: handle network error case*/
        }
    }
}

@Preview
@Composable
fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun NewsList(list: List<NewsArticle>, itemClickListener: BookmarkClickListener) {

    val scrollState = rememberLazyListState()

    LazyColumn(state = scrollState) {
        itemsIndexed(items = list) { index, item ->
            NewsItem(item, index, itemClickListener)
            Divider()
        }
    }
}

@Composable
fun NewsItem(currentArticle: NewsArticle, position: Int, itemClickListener: BookmarkClickListener) {
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { openDetails(context, currentArticle) }
        .padding(16.dp)) {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            ItemTitle(currentArticle.title, Modifier.weight(2f))
            ArticleImage(currentArticle.thumbnailUrl,
                Modifier
                    .weight(1f)
                    .size(120.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        ItemTrailText(currentArticle.articleTrail)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Author(currentArticle.author, Modifier.weight(1f))
            Section(currentArticle.section)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            DateAndTime(date = currentArticle.date, modifier = Modifier.weight(1f))
            BookmarkButton(currentArticle.isBookmarked, onBookmarkClicked = {
                itemClickListener.onBookmarkClick(position, currentArticle)
            })
            ShareButton(currentArticle.webUrl)
        }
    }
}

private fun openDetails(context : Context, article: NewsArticle) {
    val intent = Intent(context, DetailsActivity::class.java)
    intent.putExtra(CHOSEN_ARTICLE, article)
    context.startActivity(intent)
}

@Composable
fun ItemTitle(title : String, modifier: Modifier) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.DarkGray,
        modifier = modifier
    )
}

@Composable
fun ItemTrailText(trailText : String?) {
    trailText?.let {
        Text(
            text = fromHtml(it),
            fontSize = 16.sp,
        )
    }
}

@Composable
fun ArticleImage(url : String?, modifier : Modifier) {
    Image(
        painter = rememberCoilPainter(
            url,
            previewPlaceholder = R.drawable.header
        ),
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}

@Composable
fun Author(author : String?, modifier : Modifier) {
    Text(
        text = author ?: "",
        fontStyle = FontStyle.Italic,
        color = MaterialTheme.colors.primary,
        modifier = modifier
    )
}

@Composable
fun Section(section : String) {
    Surface(color = MaterialTheme.colors.primary) {
        Text(
            text = section,
            maxLines = 1,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun DateAndTime(date: String, modifier : Modifier = Modifier) {
    Text(
        text = splitDateAndTime(date),
        modifier = modifier
    )
}

@Composable
fun BookmarkButton(isBookmarked: Boolean, onBookmarkClicked: () -> Unit) {
    Icon(
        painter = painterResource(id = if (isBookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_outlined),
        contentDescription = if (isBookmarked) stringResource(R.string.cd_remove_from_bookmarks) else stringResource(
            R.string.cd_bookmark
        ),
        tint = Color.DarkGray,
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onBookmarkClicked() }
            .padding(8.dp)
    )
}

@Composable
fun ShareButton(url : String) {
    val context = LocalContext.current
    Icon(
        painter = painterResource(id = R.drawable.ic_share_dark),
        contentDescription = stringResource(R.string.cd_share),
        tint = Color.DarkGray,
        modifier = Modifier
            .clip(CircleShape)
            .clickable { shareTheLink(context, url)}
            .padding(8.dp)
    )
}

@Preview
@Composable
fun ListPreview() {
    val samplePoliticsArticle = NewsArticle(
        "politics/2021/jun/28/mps-criticise-ministers-failure-to-plan-industrial-policy",
        "MPs criticise ministers’ failure to plan industrial policy",
        "https://media.guim.co.uk/e1c5311fecbbcbf4c0267f48610f6fd30268b7f1/0_99_4000_2400/500.jpg",
        "Jillian Ambrose",
        "Committee says firms have been left guessing with UK government’s ‘short-term approach’",
        "<p>The government’s failure to set out an industrial policy agenda has left UK businesses unclear about the future at a time when a green economic recovery is urgent, according to MPs on the business, energy and industrial strategy committee.</p> <p>The group of cross-party MPs has accused the government of taking a “short-termist, unclear and unwelcome” approach to the UK’s industrial policy since scrapping the business department’s industrial strategy in March.</p> <p>In a highly critical report the committee warned that the decision to abandon the strategy, put forward by Theresa May’s government in 2017, had led to <a href=\"https://www.theguardian.com/business/2021/mar/28/industrial-strategy-uk-build-back-better-covid-government\">a “fragmented” Treasury-led plan</a> which would risk “widening the gap” between government and business.</p> <p>Darren Jones, the chair of the committee, warned that the “short-termist” approach adopted by the prime minister and the chancellor came at a time when business leaders were “crying out for long-term consistency and clarity” as the UK began to rebuild a low-carbon, post-pandemic economy outside the EU.</p> <p>The government scrapped plans to overhaul its industrial strategy in favour of a Treasury-led “plan for growth” to help revive the economy in the wake of the Covid-19 crisis.</p> <p>“The reality is that it is nothing more than a list of existing policy commitments, many of which are hopelessly delayed,” Jones said. “Long term cross-economy challenges – from problems in productivity and our ageing workforce to the opportunities presented by new technologies and the net zero transition – no longer appear to command ministerial support as long-term, cross-party, whole-of-government policy priorities.”</p> <p>The committee added that the government’s decision to disband its in-house think-tank, the <a href=\"https://www.theguardian.com/business/nils-pratley-on-finance/2021/mar/23/why-the-uk-government-really-abolished-its-own-industrial-strategy-council\">Industrial Strategy Council</a>, led by the Bank of England’s Andy Haldane, would remove the “expert, independent oversight” which had offered “important guidance” on how to shape the UK’s industrial policies.</p>  <figure class=\"element element-embed\" data-alt=\"Sign up to the daily Business Today email\">  <iframe id=\"business-today\" name=\"business-today\" src=\"https://www.theguardian.com/email/form/plaintone/business-today\" scrolling=\"no\" seamless=\"\" class=\"iframed--overflow-hidden email-sub__iframe\" height=\"52px\" frameborder=\"0\" data-component=\"email-embed--business-today\"></iframe> <figcaption>Sign up to the daily Business Today email or follow Guardian Business on Twitter at @BusinessDesk </figcaption> </figure>  <p>Jones said business leaders and parliamentarians were “now left guessing what the government’s approach is to industrial policy, with no expert oversight reporting on what ministers have actually been able to deliver”.</p> <p>In response a government spokesperson said that since the industrial strategy was published more than four years ago the government had legislated to end the UK’s contribution to climate crisis by becoming a net-zero carbon economy by 2050, and was continuing to fight the Covid-19 pandemic while forging a new path outside the EU.</p> <p>“That’s why it was right to change our approach too, with our new plan for growth setting out the opportunities we’ll seize across the UK to drive economic growth, create jobs and support British industry as we level up and build back better out of this pandemic,” the spokesperson said.</p>",
        "Jun 28, 2021T04:01",
        "https://www.theguardian.com/politics/2021/jun/28/mps-criticise-ministers-failure-to-plan-industrial-policy",
        "politics",
        false
    )
    val dummyClickListener = object : BookmarkClickListener {
        override fun onBookmarkClick(position: Int, article: NewsArticle) {}

    }
    NewsItem(currentArticle = samplePoliticsArticle, 0, dummyClickListener)
}

@Preview
@Composable
fun EmptyScreen() {
    Box(modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center) {
        Text(text = "No results found")
    }
}

interface BookmarkClickListener {
    fun onBookmarkClick(position : Int, article : NewsArticle)
}