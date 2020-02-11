package com.jandreasian.aroundme.newPost

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jandreasian.aroundme.network.Posts

class NewPostViewModel(post: Posts, app: Application) : AndroidViewModel(app) {

    private val db = FirebaseFirestore.getInstance()

    private val _eventNewPostUploaded = MutableLiveData<Boolean>()
    val eventNewPostUploaded: LiveData<Boolean>
        get() = _eventNewPostUploaded

    private val _post = MutableLiveData<Posts>()

    // The external LiveData for the SelectedImage
    val post: LiveData<Posts>
        get() = _post

    init {
        _post.value = post
    }

    fun newPost(post: Posts) {
        if(post.imgSrcUrl == null) return
        val imageUri = Uri.parse(post.imgSrcUrl)
        val storageRef = FirebaseStorage.getInstance().getReference("/images/${post.id}")

        val uploadTask = storageRef.putFile(imageUri)
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            Log.d("NewPage", "ImageUpload Failed: ${it}")

        }.addOnSuccessListener {
            Log.d("NewPage", "ImageUploaded: ${it.metadata?.path}")
        }

        val post = Posts(post.id, post.caption, storageRef.toString())

        db.collection("posts").document(post.id).set(post)
        onHomePage()
    }

    fun onHomePage() {
        _eventNewPostUploaded.value = true
    }

    fun onHomePageComplete() {
        _eventNewPostUploaded.value = false
    }

}
