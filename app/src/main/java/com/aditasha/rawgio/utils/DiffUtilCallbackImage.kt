package com.aditasha.rawgio.utils

import androidx.recyclerview.widget.DiffUtil

class DiffUtilCallbackImage(
    private val oldData: MutableList<String>,
    private val newData: MutableList<String>
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
        return oldData[oldItemPosition] == newData[newItemPosition]
    }
}