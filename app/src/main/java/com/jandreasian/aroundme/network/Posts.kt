package com.jandreasian.aroundme.network

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Posts(
    val id: String,
    val caption: String,
    val imgSrcUrl: String): Parcelable