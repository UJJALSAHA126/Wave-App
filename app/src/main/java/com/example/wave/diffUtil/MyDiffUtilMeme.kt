package com.example.wave.diffUtil

import androidx.recyclerview.widget.DiffUtil

class MyDiffUtilMeme(private val oldUrl: ArrayList<String>, private val newUrl: ArrayList<String>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldUrl.size
    }

    override fun getNewListSize(): Int {
        return newUrl.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldUrl[oldItemPosition] == newUrl[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldUrl[oldItemPosition] == newUrl[newItemPosition]
    }
}