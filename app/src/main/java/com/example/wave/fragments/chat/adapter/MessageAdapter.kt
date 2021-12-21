package com.example.wave.fragments.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.wave.R
import com.example.wave.ShowDP.Companion.showDialog
import com.example.wave.databinding.ChatLayoutBinding
import com.example.wave.diffUtil.MyDiffUtilMessages
import com.example.wave.model.Messages
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(private val context: Context, private val recyclerView: RecyclerView) :
    RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {

    private val allMessages: ArrayList<Messages> = ArrayList()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

//    private val anim0: Animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_left)
//    private val anim1: Animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_right)

    inner class MyViewHolder(binding: ChatLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        //        val fullChatLayout = binding.fullChatLayout
        val myImageView = binding.myImage
        val myTextView = binding.myText
        val friendImageView = binding.friendImage
        val friendTextView = binding.friendText

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ChatLayoutBinding =
            ChatLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

//        Log.d("UPDATE_lIST_NO. ", position.toString())

        val msg = allMessages[position]
        holder.myImageView.alpha = 0F
        holder.myTextView.alpha = 0F
        holder.friendImageView.alpha = 0F
        holder.friendTextView.alpha = 0F
        holder.myImageView.isClickable = false
        holder.friendImageView.isClickable = false

        var message = ""
        for (i in msg.message.indices) {
            message += msg.message[i] - 65
        }

        if (msg.senderUID == auth.uid!!) {
            holder.myImageView.alpha = 1F
            holder.myTextView.alpha = 1F
            holder.myImageView.isClickable = true
            holder.myTextView.text = message

            holder.myImageView.load(msg.senderPhoto) {
                placeholder(R.drawable.placeholder_image)
            }
            holder.myImageView.setOnClickListener {
                showDialog(context, msg.senderPhoto, msg.senderName)
            }
        } else {
            holder.friendImageView.alpha = 1F
            holder.friendTextView.alpha = 1F
            holder.friendImageView.isClickable = true
            holder.friendTextView.text = message

            holder.friendImageView.load(msg.senderPhoto) {
                placeholder(R.drawable.placeholder_image)
            }
            holder.friendImageView.setOnClickListener {
                showDialog(context, msg.senderPhoto, msg.senderName)
            }
        }
    }

    fun setMessages(newAllMessages: ArrayList<Messages>) {

//        Log.d("SIZE_MSG", allMessages.size.toString() + " " + newAllMessages.size.toString())

        val diffUtil = MyDiffUtilMessages(allMessages, newAllMessages)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        allMessages.clear()
        allMessages.addAll(newAllMessages)

        diffResult.dispatchUpdatesTo(this)

//        notifyItemInserted(itemCount + 1)

        recyclerView.post {
            if (itemCount > 5)
                recyclerView.smoothScrollToPosition(itemCount)
        }

//        Log.d("SIZE_MSG ->", allMessages.size.toString() + " " + newAllMessages.size.toString())
    }

    override fun getItemCount(): Int {
        return allMessages.size
    }

}