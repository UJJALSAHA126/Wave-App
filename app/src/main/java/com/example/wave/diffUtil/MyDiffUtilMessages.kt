package com.example.wave.diffUtil

import androidx.recyclerview.widget.DiffUtil
import com.example.wave.model.Messages

class MyDiffUtilMessages(
    private val oldMessage: ArrayList<Messages>,
    private val newMessage: ArrayList<Messages>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldMessage.size
    }

    override fun getNewListSize(): Int {
        return newMessage.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldMessage[oldItemPosition]
        val new = newMessage[newItemPosition]
        return (old.time == new.time && old.senderUID == new.senderUID)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldMessage[oldItemPosition]
        val new = newMessage[newItemPosition]
        return when {
            old.message != new.message -> false
            old.senderUID != new.senderUID -> false
            old.senderPhoto != new.senderPhoto -> false
            old.senderName != new.senderName -> false

            else -> true
        }
    }
}