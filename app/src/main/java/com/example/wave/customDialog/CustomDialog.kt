package com.example.wave.customDialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.example.wave.R

class CustomDialog(private val context: Context) {

    private var dialog: Dialog? = null
    var flag = true

    fun startLoading() {
        flag = true
        dialog = Dialog(context)
        dialog?.let {
            it.setContentView(R.layout.custom_dialog)
            it.setCancelable(false)
            it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.show()
        }
    }

    fun stopLoading() {
        dialog?.dismiss()
    }

}