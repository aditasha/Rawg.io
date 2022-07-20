package com.aditasha.rawgio.ui.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.text.util.Linkify
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.aditasha.rawgio.R
import com.aditasha.rawgio.core.GlideApp
import com.aditasha.rawgio.core.presentation.model.GamePresentation
import com.aditasha.rawgio.databinding.ActivityDetailBinding
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val args: DetailActivityArgs by navArgs()
    private lateinit var game: GamePresentation
    private val imageAdapter = ImageAdapter()
    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.materialToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.materialToolbar.setNavigationOnClickListener {
            finish()
        }

        game = args.game
        binding.toolbarLayout.title = game.name

        fetchDetail()
        linkTextView()
        fabClickListener()
    }

    private fun fetchDetail() {
        val color =
            ContextCompat.getColor(this@DetailActivity, com.aditasha.rawgio.core.R.color.white)
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

        val text =
            game.description?.let { HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_COMPACT) }
        binding.desc.text = getString(R.string.blank, text)

        val platforms = game.platforms?.let { joinString(it) }
        binding.platforms.text = platforms

        var genre = game.genre?.let { joinString(it) }
        if (genre?.isEmpty() == true) genre = getString(R.string.not_added)
        binding.genres.text = genre

        val developer = game.developers?.let { joinString(it) }
        binding.developers.text = developer

        var publisher = game.publishers?.let { joinString(it) }
        if (publisher?.isEmpty() == true) publisher = getString(R.string.not_added)
        binding.publishers.text = publisher

        val formattedDate = game.release?.let { dateFormat(it) }
        binding.released.text = formattedDate

        var web = game.website
        if (web?.isEmpty() == true) web = getString(R.string.not_added)
        binding.web.text = web

        binding.screenshotRecycler.apply {
            adapter = imageAdapter
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        }
        game.screenshots?.let { imageAdapter.addData(it) }

    }

    private fun joinString(input: MutableList<String>): String {
        return input.joinToString(". ") { it }
    }

    private fun linkTextView() {
        val movement = BetterLinkMovementMethod.newInstance().apply {
            setOnLinkLongClickListener { _, url ->
                val clipboard = getSystemService<ClipboardManager>()
                val clip = ClipData.newPlainText("web", url)
                clipboard?.setPrimaryClip(clip)
                Toast.makeText(this@DetailActivity, "Copied to clipboard", Toast.LENGTH_LONG).show()
                true
            }
        }
        Linkify.addLinks(binding.web, Linkify.WEB_URLS)
        binding.web.movementMethod = movement

        Linkify.addLinks(binding.source, Linkify.WEB_URLS)
        binding.source.movementMethod = movement
    }

    private fun dateFormat(text: String): String {
        val date = LocalDate.parse(text)
        val formatter = DateTimeFormatter.ofPattern("d MMM yyyy")
        return date.format(formatter)
    }

    private fun fabClickListener() {
        lifecycleScope.launchWhenCreated {
            detailViewModel.getFavorite(game.id).collectLatest { isFavorite ->
                if (!isFavorite) {
                    binding.fab.icon =
                        getDrawable(com.aditasha.rawgio.core.R.drawable.icon_favorites_border)
                    binding.fab.setOnClickListener {
                        detailViewModel.addFavorite(
                            game.id,
                            game.name,
                            game.background,
                            game.screenshots
                        )
                        val message = getString(R.string.add_favorite, game.name)
                        Toast.makeText(this@DetailActivity, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    binding.fab.icon =
                        getDrawable(com.aditasha.rawgio.core.R.drawable.icon_favorites)
                    binding.fab.setOnClickListener {
                        detailViewModel.deleteFavorite(game.id)
                        val message = getString(R.string.remove_favorite, game.name)
                        Toast.makeText(this@DetailActivity, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
