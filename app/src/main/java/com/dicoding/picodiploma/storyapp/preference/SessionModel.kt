package com.dicoding.picodiploma.storyapp.preference

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SessionModel(
    var userId: String? = null,
    var name: String? = null,
    var token: String? = null
) : Parcelable