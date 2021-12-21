package com.example.wave.diffUtil

import androidx.recyclerview.widget.DiffUtil
import com.example.wave.model.User

class MyDiffUtilUsers(private val oldList: ArrayList<User>, private val newList: ArrayList<User>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition].uid == newList[newItemPosition].uid)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]

        return when {
            old.uid != new.uid -> false

            old.name != new.name -> false

            old.email != new.name -> false

            old.phone != new.phone -> false

            old.photo != new.photo -> false

            else -> true
        }
    }
}