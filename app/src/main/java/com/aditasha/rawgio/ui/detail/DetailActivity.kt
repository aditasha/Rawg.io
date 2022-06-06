package com.aditasha.rawgio.ui.detail

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.navigation.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.aditasha.rawgio.R
import com.aditasha.rawgio.core.GlideApp
import com.aditasha.rawgio.core.presentation.model.GamePresentation
import com.aditasha.rawgio.databinding.ActivityDetailBinding
import com.bumptech.glide.request.RequestOptions
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val args: DetailActivityArgs by navArgs()
    private lateinit var game: GamePresentation
    private val imageAdapter = ImageAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.materialToolbar))

        game = args.game
        //Log.d("test_detail", game.toString())

        val text = game.description?.let { HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_COMPACT) }
        binding.toolbarLayout.title = game.name

        val color = ContextCompat.getColor(this@DetailActivity, com.aditasha.rawgio.core.R.color.white)
        val circularProgressDrawable = CircularProgressDrawable(this@DetailActivity).apply {
            setColorSchemeColors(color)
            strokeWidth = 5f
            centerRadius = 15f
            start()
        }

        GlideApp.with(this@DetailActivity)
            .load(game.background)
            .apply(
                RequestOptions().dontTransform()
            )
            .placeholder(circularProgressDrawable)
            .into(binding.bgGame)

        binding.rating.text = game.rating.toString()
        binding.added.text = game.added.toString()

        val count = getString(R.string.rating_count, game.ratings_count.toString())
        binding.ratingCount.text = count

        binding.desc.text = getString(R.string.blank, text)

        val platforms = game.platforms?.joinToString(", ") { it }
        binding.platforms.text = platforms

        var genre = game.genre?.joinToString(", ") { it }
        if (genre?.isEmpty() == true) {
            genre = "(Not added)"
        }
        binding.genres.text = genre

        val developer = game.developers?.joinToString(", ") { it }
        binding.developers.text = developer

        var publisher = game.publishers?.joinToString(", ") { it }
        if (publisher?.isEmpty() == true) {
            publisher = "(Not added)"
        }
        binding.publishers.text = publisher

        val formattedDate = game.release?.let { dateFormat(it) }
        binding.released.text = formattedDate

        var web = game.website
        if (web?.isEmpty() == true) {
            web = "(Not added)"
        }
        binding.web.text = web

        binding.screenshotRecycler.apply {
            adapter = imageAdapter
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        }
        game.screenshots?.let { imageAdapter.addData(it) }
    }

    private fun dateFormat(text: String): String {
        val date = LocalDate.parse(text)
        val formatter = DateTimeFormatter.ofPattern("d MMM yyyy")
        return date.format(formatter)
    }
}