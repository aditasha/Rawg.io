package com.aditasha.rawgio.core.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.aditasha.rawgio.core.GlideApp
import com.aditasha.rawgio.core.R
import com.aditasha.rawgio.core.databinding.ItemListGamesBinding
import com.aditasha.rawgio.core.presentation.model.GamePresentation
import com.aditasha.rawgio.core.utils.DiffUtilCallback

class FavoriteGameAdapter : RecyclerView.Adapter<FavoriteGameAdapter.ListViewHolder>() {
    private var listGame: ArrayList<GamePresentation> = ArrayList()

    fun addData(data: ArrayList<GamePresentation>) {
        val diffCallback = DiffUtilCallback(listGame, data)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        listGame.clear()
        listGame.addAll(data)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemListGamesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = listGame[position]
        holder.bind(data)
    }

    class ListViewHolder(var binding: ItemListGamesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: GamePresentation) {
            val color = ContextCompat.getColor(itemView.context, R.color.white)

            val circularProgressDrawable = CircularProgressDrawable(itemView.context)
            circularProgressDrawable.setColorSchemeColors(color)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 15f
            circularProgressDrawable.start()

            GlideApp
                .with(itemView.context)
                .load(data.background)
                .placeholder(circularProgressDrawable)
                .into(binding.bgGame)

            binding.gameName.text = data.name
            binding.layout.added.text =
                itemView.resources.getString(R.string.added_list_games, data.added.toString())
            binding.layout.released.text = data.release

            val genres = data.genre?.joinToString(", ") { it }
            binding.layout.genres.text = genres
        }
    }

    override fun getItemCount(): Int = listGame.size
}
