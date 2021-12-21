package com.example.wave.customDialog

import android.content.Context
import android.widget.Toast
import com.example.wave.R
import io.github.muddz.styleabletoast.StyleableToast

class CustomToast(private val context: Context) {
    fun showToast(message:String){
        StyleableToast.makeText(context,message,Toast.LENGTH_SHORT, R.style.myToast).show()
    }
}