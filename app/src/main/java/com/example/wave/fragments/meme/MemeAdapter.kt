package com.example.wave.fragments.meme

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.wave.DownloadImage
import com.example.wave.R
import com.example.wave.databinding.CustomMemeLayoutBinding
import com.example.wave.diffUtil.MyDiffUtilMeme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MemeAdapter(val context: Context, private val recyclerView: RecyclerView) :
    RecyclerView.Adapter<MemeAdapter.MemeViewHolder>() {

    private val memeUrls = ArrayList<String>()
    private val lastUrls = ArrayList<String>()
    private val downloadImage = DownloadImage(context)

    inner class MemeViewHolder(binding: CustomMemeLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val memeImage: ImageView = binding.memeImage
        val layout: ConstraintLayout = binding.memeLayout
        val downloadButton: ImageView = binding.memeDownloadButton

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val binding =
            CustomMemeLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MemeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {

        val currentImageUrl = memeUrls[position]

        GlobalScope.launch(Dispatchers.IO) {
            holder.memeImage.load(currentImageUrl)
        }

        holder.layout.setOnClickListener {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.popup_meme_lay)
            val image: ImageView = dialog.findViewById(R.id.popupMeme1)
            image.load(currentImageUrl)
            dialog.show()
        }

        holder.downloadButton.setOnClickListener {
            downloadImage.downloadPhoto(currentImageUrl)
        }
    }

    override fun getItemCount(): Int {
        return memeUrls.size
    }

    fun setUrls(allUrl: ArrayList<String>) {
        memeUrls.clear()
        memeUrls.addAll(allUrl)
        notifyTheAdapter(lastUrls, memeUrls, 1)
        lastUrls.clear()
        lastUrls.addAll(memeUrls)
    }

    fun addUrlFirst(newUrl: String) {
        memeUrls.add(0, newUrl)
        notifyTheAdapter(lastUrls, memeUrls, 0)
        lastUrls.clear()
        lastUrls.addAll(memeUrls)
    }

    fun addUrlEnd(newUrl: String) {
        memeUrls.add(newUrl)
        notifyTheAdapter(lastUrls, memeUrls, 1)
        lastUrls.clear()
        lastUrls.addAll(memeUrls)
    }

    fun ifContains(url: String): Boolean {
        return memeUrls.contains(url)
    }

    private fun notifyTheAdapter(old: ArrayList<String>, new: ArrayList<String>, mode: Int) {
        val diffUtil = MyDiffUtilMeme(old, new)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        diffResult.dispatchUpdatesTo(this)
        if (mode == 0) {
            recyclerView.post {
                recyclerView.smoothScrollToPosition(0)
            }
        }
    }
}