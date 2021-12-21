package com.example.wave

import android.app.Dialog
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import coil.load
import coil.request.CachePolicy

class ShowDP private constructor(){

    companion object{
        fun showDialog(context: Context,imgUrl: String,name: String){
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.dialog_dp)
            val dp = dialog.findViewById<ImageView>(R.id.dialogDP)
            val dpName: TextView = dialog.findViewById(R.id.dialogName)
            dpName.text = name
            dp.load(imgUrl){
                diskCachePolicy(CachePolicy.ENABLED)
            }
            dialog.show()
        }
    }
}