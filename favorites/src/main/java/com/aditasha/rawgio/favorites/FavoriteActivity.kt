package com.aditasha.rawgio.favorites

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.aditasha.rawgio.core.data.Resource
import com.aditasha.rawgio.core.presentation.FavoriteGameAdapter
import com.aditasha.rawgio.core.presentation.model.FavoritePresentation
import com.aditasha.rawgio.core.utils.DataMapper
import com.aditasha.rawgio.di.FavoritesModuleDependencies
import com.aditasha.rawgio.favorites.databinding.ActivityFavoriteBinding
import com.aditasha.rawgio.ui.detail.DetailActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class FavoriteActivity : AppCompatActivity() {
    @Inject
    lateinit var factory: ViewModelFactory

    private val favoriteViewModel: FavoriteViewModel by viewModels { factory }

    private lateinit var binding: ActivityFavoriteBinding

    override fun onResume() {
        super.onResume()
        favoriteViewModel.getAllFavorite()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerFavoritesComponent.builder()
            .context(this@FavoriteActivity)
            .appDependencies(
                EntryPointAccessors.fromApplication(
                    this@FavoriteActivity,
                    FavoritesModuleDependencies::class.java
                )
            )
            .build()
            .inject(this@FavoriteActivity)

        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        if (savedInstanceState == null) {
//            val navHostFragment =
//                supportFragmentManager.findFragmentById(R.id.favorite) as NavHostFragment
//            val navController = navHostFragment.navController
//            navController.navigate(R.id.favoriteFragment)
//        }

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        val favoriteGameAdapter = FavoriteGameAdapter()
        setRecycler(favoriteGameAdapter)
        fetchData(favoriteGameAdapter)
        clickListener(favoriteGameAdapter)
    }

    private fun setRecycler(favoriteGameAdapter: FavoriteGameAdapter) {
        binding.apply {
            gameRecycler.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@FavoriteActivity)
                adapter = favoriteGameAdapter
            }
        }
    }

    private fun fetchData(favoriteGameAdapter: FavoriteGameAdapter) {
        lifecycleScope.launchWhenCreated {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                favoriteViewModel.favoriteFlow.collectLatest { list ->
                    if (list.isEmpty()) {
                        binding.emptyFavorite.isVisible = true
                        binding.lottie.isVisible = true
                    }
                    val array = arrayListOf<FavoritePresentation>()
                    for (l in list) {
                        array.add(l)
                    }
                    favoriteGameAdapter.addData(array)
                }
            }
        }
    }

    private fun clickListener(favoriteGameAdapter: FavoriteGameAdapter) {
        favoriteGameAdapter.setOnItemClickCallback(object :
            FavoriteGameAdapter.OnItemClickCallback {

            override fun onItemClicked(fav: FavoritePresentation) {
                lifecycleScope.launchWhenCreated {
                    favoriteViewModel.gameDetail(fav.id)
                        .collectLatest {
                            when (it) {
                                is Resource.Loading -> {
                                    showLoading(true)
                                    showFailed(false, "")
                                }

                                is Resource.Error -> {
                                    showLoading(false)
                                    showFailed(true, it.message.toString())
                                }

                                else -> {
                                    showLoading(false)
                                    showFailed(false, "")

                                    if (it.data != null) {
                                        val gameDetail =
                                            DataMapper.mapDomainToPresentation(it.data!!)
                                        gameDetail.screenshots = fav.screenshots

                                        val intent = Intent(this@FavoriteActivity, DetailActivity::class.java)
                                        intent.putExtra(DetailActivity.GAME_DETAIL, gameDetail)
                                        startActivity(intent)
                                    }
                                }
                            }
                        }
                }
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.apply {
                gameRecycler.isVisible = false
                loading.isVisible = true
            }
        } else {
            binding.apply {
                gameRecycler.isVisible = true
                loading.isVisible = false
            }
        }
    }

    private fun showFailed(isFailed: Boolean, e: String) {
        if (isFailed) {
            val text = getString(R.string.error, e)
            errorDialog(text).show()
        }
    }

    private fun errorDialog(e: String): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(this@FavoriteActivity)
            .setMessage(e)
            .setPositiveButton(resources.getString(R.string.close_dialog)) { dialog, _ ->
                dialog.dismiss()
            }
    }
}