package com.aditasha.rawgio.core.utils

import androidx.recyclerview.widget.DiffUtil
import com.aditasha.rawgio.core.presentation.model.GamePresentation

class DiffUtilCallback(
    private val oldData: ArrayList<GamePresentation>,
    private val newData: ArrayList<GamePresentation>
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