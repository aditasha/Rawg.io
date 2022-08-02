package com.aditasha.rawgio.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.aditasha.rawgio.core.GlideApp
import com.aditasha.rawgio.core.R
import com.aditasha.rawgio.databinding.ItemListScreenshotBinding
import com.aditasha.rawgio.utils.DiffUtilCallbackImage

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ListViewHolder>() {
    private var listImage = mutableListOf<String>()

    fun addData(data: MutableList<String>) {
        val diffCallback = DiffUtilCallbackImage(listImage, data)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        listImage.clear()
        listImage.addAll(data)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemListScreenshotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = listImage[position]
        holder.bind(data)
    }

    class ListViewHolder(private var binding: ItemListScreenshotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String) {
            val color = ContextCompat.getColor(itemView.context, R.color.white)

            val circularProgressDrawable = CircularProgressDrawable(itemView.context)
            circularProgressDrawable.setColorSchemeColors(color)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 15f
            circularProgressDrawable.start()

            GlideApp
                .with(itemView.context)
                .load(data)
                .placeholder(circularProgressDrawable)
                .into(binding.ssGame)
        }
    }

    override fun getItemCount(): Int = listImage.size
}