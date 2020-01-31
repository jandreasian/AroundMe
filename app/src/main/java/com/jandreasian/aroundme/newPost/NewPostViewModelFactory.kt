package com.jandreasian.aroundme.newPost

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jandreasian.aroundme.network.Posts

class NewPostViewModelFactory(
    private val post: Posts,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewPostViewModel::class.java)) {
            return NewPostViewModel(post, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}