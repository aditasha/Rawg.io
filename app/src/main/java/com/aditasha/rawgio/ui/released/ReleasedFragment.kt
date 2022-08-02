package com.aditasha.rawgio.ui.released

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.aditasha.rawgio.databinding.FragmentHomeBinding
import com.aditasha.rawgio.ui.SharedViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy

@AndroidEntryPoint
class ReleasedFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val gameAdapter = GameListAdapter()
        setupRecycler(gameAdapter)
        setupAdapter(gameAdapter)
        fetchData(gameAdapter)
        clickListener(gameAdapter)

        return root
    }

    private fun setupRecycler(gameAdapter: GameListAdapter) {
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

        binding.swipeRefresh.setOnRefreshListener {
            gameAdapter.refresh()
            binding.swipeRefresh.isRefreshing = false
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            gameAdapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .collect {
                    val isLoading = it.refresh is LoadState.Loading
                    val isNotLoading = it.refresh is LoadState.NotLoading
                    binding.gameRecycler.post {
                        if (isLoading)
                            binding.gameRecycler.smoothScrollToPosition(0)
                        binding.gameRecycler.isVisible = isNotLoading
                        binding.loading.isVisible = isLoading
                    }
                }
        }
    }

    private fun setupAdapter(gameAdapter: GameListAdapter) {
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

    private fun fetchData(gameAdapter: GameListAdapter) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.gameList
                    .collectLatest {
                        gameAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                    }
            }
        }
    }

    private fun clickListener(gameAdapter: GameListAdapter) {
        gameAdapter.setOnItemClickCallback(object : GameListAdapter.OnItemClickCallback {

            override fun onItemClicked(game: GamePresentation) {
                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    sharedViewModel.gameDetail(game.id)
                        .collectLatest {
                            when (it) {
                                is Resource.Loading -> {
                                    showLoading(true)
                                    showFailed(false, "", gameAdapter)
                                }

                                is Resource.Error -> {
                                    showLoading(false)
                                    showFailed(true, it.message.toString(), gameAdapter)
                                }

                                else -> {
                                    showLoading(false)
                                    showFailed(false, "", gameAdapter)

                                    if (it.data != null) {
                                        val gameDetail =
                                            DataMapper.mapDomainToPresentation(it.data!!)
                                        gameDetail.screenshots = game.screenshots

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

    private fun showFailed(isFailed: Boolean, e: String, gameAdapter: GameListAdapter) {
        if (isFailed) {
            val text = getString(R.string.error, e)
            errorDialog(text, gameAdapter).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.apply {
                gameRecycler.isVisible = false
                loading.isVisible = true
                swipeRefresh.isRefreshing = true
            }
        } else {
            binding.apply {
                gameRecycler.isVisible = true
                loading.isVisible = false
                swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun errorDialog(e: String, gameAdapter: GameListAdapter): MaterialAlertDialogBuilder {
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