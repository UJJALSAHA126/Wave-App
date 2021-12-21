package com.example.wave.fragments.chat.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatDetails(
    val receiverName: String,
    val receiverImage: String,
    val receiverUID: String,
    val myName: String,
    val myImage: String,
    val myUID: String
): Parcelable
