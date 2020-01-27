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
import java.util.*

class NewPostViewModel(imageSrcUri: Uri, app: Application) : AndroidViewModel(app) {

    private val db = FirebaseFirestore.getInstance()

    private val _eventNewPostUploaded = MutableLiveData<Boolean>()
    val eventNewPostUploaded: LiveData<Boolean>
        get() = _eventNewPostUploaded

    private val _selectedImage = MutableLiveData<Uri>()

    // The external LiveData for the SelectedProperty
    val selectedImage: LiveData<Uri>
        get() = _selectedImage

    init {
        _selectedImage.value = imageSrcUri
    }

    fun newPost(image_uri: Uri?, post: Posts) {
        if(image_uri == null) return
        val uuid: String = UUID.randomUUID().toString()
        val fileName = UUID.randomUUID().toString()
        val storageRef = FirebaseStorage.getInstance().getReference("/images/$fileName")
        storageRef.putFile(image_uri!!)
            .addOnSuccessListener {
                Log.d("HomePageFragment", "ImageUploaded: ${it.metadata?.path}")
            }
        val post = Posts(post.caption, "gs://aroundme-7b5fa.appspot.com/images/$fileName")
        db.collection("posts").document(uuid).set(post)
        onHomePage()
    }

    fun onHomePage() {
        _eventNewPostUploaded.value = true
    }

    fun onHomePageComplete() {
        _eventNewPostUploaded.value = false
    }

}
