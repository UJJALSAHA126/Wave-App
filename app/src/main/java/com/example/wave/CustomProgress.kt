package com.example.wave

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView

class CustomProgress(private val context: Context, private val view: View) {

    private val textView: TextView = view.findViewById(R.id.textBtn)
    private val progress: ProgressBar = view.findViewById(R.id.progressBtn)
    private val layout: RelativeLayout = view.findViewById(R.id.logInButton)
    private val card : CardView = view.findViewById(R.id.cardVBtn)

    fun reset(text: String) {
        view.isActivated = true
        view.isClickable = true
        textView.text = text
        progress.alpha = 0F
        layout.setBackgroundColor(card.resources.getColor(R.color.purple_700))
    }

    fun clicked() {
        view.isActivated = false
        view.isClickable = false
        "Please Wait...".also { textView.text = it }
        progress.alpha = 1F
        layout.setBackgroundColor(card.resources.getColor(R.color.purple_200))
    }

    fun done() {
        view.isActivated = false
        view.isClickable = false
        "Done".also { textView.text = it }
        progress.alpha = 0F
        layout.setBackgroundColor(card.resources.getColor(R.color.teal_200))
    }

}