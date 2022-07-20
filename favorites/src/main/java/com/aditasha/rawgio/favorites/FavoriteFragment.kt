package com.aditasha.rawgio.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aditasha.rawgio.core.data.Resource
import com.aditasha.rawgio.core.presentation.FavoriteGameAdapter
import com.aditasha.rawgio.core.presentation.model.FavoritePresentation
import com.aditasha.rawgio.core.utils.DataMapper
import com.aditasha.rawgio.di.FavoritesModuleDependencies
import com.aditasha.rawgio.favorites.databinding.FragmentFavoriteBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class FavoriteFragment : Fragment() {

    @Inject
    lateinit var factory: ViewModelFactory

    private val favoriteViewModel: FavoriteViewModel by activityViewModels { factory }

    private lateinit var binding: FragmentFavoriteBinding
    private val favoriteGameAdapter = FavoriteGameAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        DaggerFavoritesComponent.builder()
            .context(requireActivity())
            .appDependencies(
                EntryPointAccessors.fromApplication(
                    requireActivity(),
                    FavoritesModuleDependencies::class.java
                )
            )
            .build()
            .inject(this)

        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setRecycler()
        fetchData()
        clickListener()
        return root
    }

    override fun onResume() {
        super.onResume()
        favoriteViewModel.getAllFavorite()
    }

    private fun setRecycler() {
        binding.apply {
            gameRecycler.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = favoriteGameAdapter
            }
        }
    }

    private fun fetchData() {
        lifecycleScope.launchWhenCreated {
            favoriteViewModel.favoriteFlow.collectLatest { list ->
                if (list.isEmpty()) {
                    binding.emptyFavorite.isVisible = true
                }
                val array = arrayListOf<FavoritePresentation>()
                list.forEach { array.add(it) }
                favoriteGameAdapter.addData(array)
            }
        }
    }

    private fun clickListener() {
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

                                        Toast.makeText(
                                            requireActivity(),
                                            "succes getting data",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val action =
                                            FavoriteFragmentDirections.actionFavoriteFragmentToDetailActivity(
                                                gameDetail
                                            )
                                        findNavController().navigate(action)
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
        return MaterialAlertDialogBuilder(requireActivity())
            .setMessage(e)
            .setPositiveButton(resources.getString(R.string.close_dialog)) { dialog, _ ->
                dialog.dismiss()
            }
    }
}