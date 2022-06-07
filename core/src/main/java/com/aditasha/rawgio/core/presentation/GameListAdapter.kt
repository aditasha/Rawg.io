package com.aditasha.rawgio.core.presentation

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.aditasha.rawgio.core.GlideApp
import com.aditasha.rawgio.core.R
import com.aditasha.rawgio.core.databinding.ItemListGamesBinding
import com.aditasha.rawgio.core.presentation.model.GamePresentation
import com.bumptech.glide.request.RequestOptions
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GameListAdapter :
    PagingDataAdapter<GamePresentation, GameListAdapter.MyViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback
    private var expandedPosition = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemListGamesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data, position)
        }
    }

    inner class MyViewHolder(val binding: ItemListGamesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: GamePresentation, position: Int) {
            val color = ContextCompat.getColor(itemView.context, R.color.white)
            val circularProgressDrawable = CircularProgressDrawable(itemView.context).apply {
                setColorSchemeColors(color)
                strokeWidth = 5f
                centerRadius = 15f
                start()
            }

            GlideApp.with(itemView.context)
                .load(data.background)
                .apply(
                    RequestOptions().dontTransform()
                )
                .placeholder(circularProgressDrawable)
                .into(binding.bgGame)

            binding.gameName.text = data.name
            binding.layout.added.text =
                itemView.resources.getString(R.string.added_list_games, data.added.toString())

            val formattedDate = data.release?.let { dateFormat(it) }
            binding.layout.released.text =
                itemView.resources.getString(R.string.released_list_games, formattedDate)

            var genres = data.genre?.joinToString(", ") { it }
            if (genres?.isEmpty() == true) {
                genres = "(Not added)"
            }
            binding.layout.genres.text =
                itemView.resources.getString(R.string.genres_list_games, genres)

            itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(data)
            }

            if (expandedPosition.contains(position)) {
                binding.expandableCheck.isChecked = true
                TransitionManager.beginDelayedTransition(itemView as ViewGroup, AutoTransition())
                binding.layout.expandableLayout.visibility = View.VISIBLE
            } else if (expandedPosition.isNotEmpty()) {
                binding.expandableCheck.isChecked = false
                TransitionManager.beginDelayedTransition(itemView as ViewGroup, AutoTransition())
                binding.layout.expandableLayout.visibility = View.GONE
            }

            binding.expandableCheck.setOnClickListener {
                if (binding.layout.expandableLayout.visibility == View.VISIBLE) {
                    binding.expandableCheck.isChecked = false
                    TransitionManager.beginDelayedTransition(
                        itemView as ViewGroup,
                        AutoTransition()
                    )
                    binding.layout.expandableLayout.visibility = View.GONE
                    expandedPosition.remove(position)
                } else if (binding.layout.expandableLayout.visibility == View.GONE) {
                    binding.expandableCheck.isChecked = true
                    TransitionManager.beginDelayedTransition(
                        itemView as ViewGroup,
                        AutoTransition()
                    )
                    binding.layout.expandableLayout.visibility = View.VISIBLE
                    expandedPosition.add(position)
                }
            }
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(game: GamePresentation)
    }

    private fun dateFormat(text: String): String {
        val date = LocalDate.parse(text)
        val formatter = DateTimeFormatter.ofPattern("d MMM yyyy")
        return date.format(formatter)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GamePresentation>() {
            override fun areItemsTheSame(
                oldItem: GamePresentation,
                newItem: GamePresentation
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: GamePresentation,
                newItem: GamePresentation
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}