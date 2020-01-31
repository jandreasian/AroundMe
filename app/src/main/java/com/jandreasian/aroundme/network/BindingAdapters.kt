package com.jandreasian.aroundme.network

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.jandreasian.aroundme.homePage.PostsAdapter

val storage = FirebaseStorage.getInstance()

/**
 * Uses the Glide library to load an image by URI into an [ImageView] for the Recyclerview on HomePage
 */
@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String) {

    imgUrl?.let {
        val gsReference = storage.getReferenceFromUrl(imgUrl)
        Log.d("BindingAdapter: ",gsReference.toString())
        Glide
            .with(imgView.context)
            .load(gsReference)
            .override(1200,1800)
            .into(imgView)
    }
}

/**
 * Uses the Glide library to load an image by URI into an [ImageView] for NewPost
 */
@BindingAdapter("imageUri")
fun bind(imgView: ImageView, imgUri: String) {

    imgUri?.let {
        val uri = Uri.parse(imgUri)
        Log.d("BindingAdapter: ",uri.toString())
        Glide
            .with(imgView.context)
            .load(uri)
            .into(imgView)
    }
}

/**
 * When there is no Posts data (data is null), hide the [RecyclerView], otherwise show it.
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Posts>?) {
    val adapter = recyclerView.adapter as PostsAdapter
    adapter.submitList(data)
}