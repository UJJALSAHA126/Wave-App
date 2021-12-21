package com.example.wave.fragments.profile

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfileValues(
    var uid: String,
    var name: String,
    var phone: String,
    var email: String,
    var Photo: String
):Parcelable
