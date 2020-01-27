package com.jandreasian.aroundme.newPost

import android.app.Application
import android.icu.lang.UCharacter.GraphemeClusterBreak.V
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NewPostViewModelFactory(
    private val imageSrcUrl: Uri,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewPostViewModel::class.java)) {
            return NewPostViewModel(imageSrcUrl, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}