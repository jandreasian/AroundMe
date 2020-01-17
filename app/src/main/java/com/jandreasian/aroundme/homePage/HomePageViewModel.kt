package com.jandreasian.aroundme.homePage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.jandreasian.aroundme.network.Posts

class HomePageViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _caption = MutableLiveData<String>()
    val caption: LiveData<String>
        get() = _caption
    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String>
        get() = _imageUrl
    var posts : MutableLiveData<List<Posts>> = MutableLiveData()

    //This will grab all of the posts that are contained in the Posts collection
    fun getAllPosts(): LiveData<List<Posts>> {
        var postList : MutableList<Posts> = mutableListOf()
        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    postList.add(Posts(document.id, document.get("Caption").toString(),document.get("ImageURL").toString()))
                    _caption.value = document.get("Caption").toString()
                    _imageUrl.value = document.get("ImageURL").toString()
                }
                posts.value = postList
            }
            .addOnFailureListener { exception ->
                Log.w("HomePageViewModel", "Error getting documents.", exception)
            }

        return posts
    }
}
