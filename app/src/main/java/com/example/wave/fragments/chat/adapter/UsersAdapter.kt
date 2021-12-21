package com.example.wave.fragments.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.example.wave.R
import com.example.wave.ShowDP.Companion.showDialog
import com.example.wave.databinding.CustomUserListBinding
import com.example.wave.diffUtil.MyDiffUtilUsers
import com.example.wave.fragments.chat.listener.ClickedOnChat
import com.example.wave.model.User

class UsersAdapter(val context: Context, private val listener: ClickedOnChat) :
    RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {

    private val users = ArrayList<User>()

    inner class MyViewHolder(val binding: CustomUserListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val displayPicture = binding.customProfilePhoto
        val name = binding.customUserName
        val number = binding.customUserPhone
        val layout = binding.customListLay
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: CustomUserListBinding =
            CustomUserListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val animation = if (position % 2 == 0) {
            AnimationUtils.loadAnimation(context, R.anim.slide_in_from_left_chat_user)
        } else {
            AnimationUtils.loadAnimation(context, R.anim.slide_in_from_right_chat_user)
        }

        holder.layout.startAnimation(animation)

        val currentUser = users[position]

        holder.displayPicture.load(currentUser.photo) {
            diskCachePolicy(CachePolicy.ENABLED)
            placeholder(R.drawable.placeholder_image)
        }
        holder.name.text = currentUser.name

        currentUser.phone.also {
            holder.number.text = it
        }

        holder.displayPicture.setOnClickListener {
            showDialog(context, currentUser.photo, currentUser.name)
        }

        holder.layout.setOnClickListener {
            listener.clicked(currentUser.name, currentUser.photo, currentUser.uid)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun setData(newUsers: ArrayList<User>) {
        val diffUtil = MyDiffUtilUsers(users, newUsers)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        users.clear()
        users.addAll(newUsers)
        diffResult.dispatchUpdatesTo(this)
    }
}