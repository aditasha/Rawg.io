package com.aditasha.rawgio.ui.released

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aditasha.rawgio.MainActivity.Companion.ADDED
import com.aditasha.rawgio.MainActivity.Companion.DEFAULT
import com.aditasha.rawgio.MainActivity.Companion.RELEASED
import com.aditasha.rawgio.MainActivity.Companion.SEARCH
import com.aditasha.rawgio.R
import com.aditasha.rawgio.core.data.Resource
import com.aditasha.rawgio.core.presentation.GameListAdapter
import com.aditasha.rawgio.core.presentation.LoadingStateAdapter
import com.aditasha.rawgio.core.presentation.model.GamePresentation
import com.aditasha.rawgio.core.utils.DataMapper
import com.aditasha.rawgio.databinding.FragmentReleasedBinding
import com.aditasha.rawgio.ui.SharedViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*

@AndroidEntryPoint
class ReleasedFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val gameAdapter = GameListAdapter()

    private var _binding: FragmentReleasedBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReleasedBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecycler()
        setupAdapter()
        fetchData()
        clickListener()

        return root
    }

    private fun setupRecycler() {
        binding.apply {
            gameRecycler.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = gameAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        gameAdapter.retry()
                    }
                )
            }
        }

        binding.swipeRefresh.setOnRefreshListener { gameAdapter.refresh() }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            gameAdapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .collect {
                    val isLoading = it.refresh is LoadState.Loading
                    val isNotLoading = it.refresh is LoadState.NotLoading

                    binding.swipeRefresh.isRefreshing = isLoading
                    binding.gameRecycler.post {
                        if (isLoading)
                            binding.gameRecycler.smoothScrollToPosition(0)
                        binding.gameRecycler.isVisible = isNotLoading
                    }
                }
        }
    }

    private fun setupAdapter() {
        gameAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.requestedList.collectLatest {
                    if (it != RELEASED) {
                        if (it == ADDED || it == DEFAULT) {
                            sharedViewModel.addQuery(RELEASED)
                            gameAdapter.refresh()
                        } else {
                            sharedViewModel.addQuery(SEARCH, RELEASED)
                            gameAdapter.refresh()
                        }
                    }
                }
            }
        }
    }

    private fun fetchData() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.gameList
                    .collectLatest {
                        gameAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                    }
            }
        }
    }

    private fun clickListener() {
        gameAdapter.setOnItemClickCallback(object : GameListAdapter.OnItemClickCallback {

            override fun onItemClicked(game: GamePresentation) {
                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    sharedViewModel.gameDetail(game.id)
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
                                        gameDetail.screenshots = game.screenshots

                                        Toast.makeText(
                                            requireActivity(),
                                            "succes getting data",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        val action =
                                            ReleasedFragmentDirections.actionNavigationReleasedToDetailActivity(
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

    private fun showFailed(isFailed: Boolean, e: String) {
        if (isFailed) {
            val text = getString(R.string.error, e)
            errorDialog(text).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.apply {
                searchTextLayout.isVisible = false
                gameRecycler.isVisible = false
                loading.isVisible = true
            }
        } else {
            binding.apply {
                searchTextLayout.isVisible = true
                gameRecycler.isVisible = true
                loading.isVisible = false
            }
        }
    }

    private fun errorDialog(e: String): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(requireActivity())
            .setMessage(e)
            .setPositiveButton(resources.getString(R.string.close_dialog)) { dialog, _ ->
                gameAdapter.retry()
                dialog.dismiss()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}