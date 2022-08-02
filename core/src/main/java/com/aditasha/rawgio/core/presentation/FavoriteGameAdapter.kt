package com.aditasha.rawgio.core.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.aditasha.rawgio.core.GlideApp
import com.aditasha.rawgio.core.R
import com.aditasha.rawgio.core.databinding.ItemListFavoriteBinding
import com.aditasha.rawgio.core.presentation.model.FavoritePresentation
import com.aditasha.rawgio.core.utils.DiffUtilCallback

class FavoriteGameAdapter : RecyclerView.Adapter<FavoriteGameAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback
    private var listGame: ArrayList<FavoritePresentation> = ArrayList()

    fun addData(data: ArrayList<FavoritePresentation>) {
        val diffCallback = DiffUtilCallback(listGame, data)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        listGame.clear()
        listGame.addAll(data)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemListFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = listGame[position]
        holder.bind(data)
    }

    inner class ListViewHolder(private var binding: ItemListFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: FavoritePresentation) {
            val color = ContextCompat.getColor(itemView.context, R.color.white)

            val circularProgressDrawable = CircularProgressDrawable(itemView.context)
            circularProgressDrawable.setColorSchemeColors(color)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 15f
            circularProgressDrawable.start()

            GlideApp
                .with(itemView.context)
                .load(data.picture)
                .placeholder(circularProgressDrawable)
                .into(binding.bgGame)

            binding.gameName.text = data.name

            itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(data)
            }
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(fav: FavoritePresentation)
    }

    override fun getItemCount(): Int = listGame.size
}
