package com.aditasha.rawgio.core.utils

import androidx.recyclerview.widget.DiffUtil
import com.aditasha.rawgio.core.presentation.model.FavoritePresentation

class DiffUtilCallback(
    private val oldData: ArrayList<FavoritePresentation>,
    private val newData: ArrayList<FavoritePresentation>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldData.size
    }

    override fun getNewListSize(): Int {
        return newData.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition].hashCode() == newData[newItemPosition].hashCode()
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition].id == newData[newItemPosition].id
    }
}