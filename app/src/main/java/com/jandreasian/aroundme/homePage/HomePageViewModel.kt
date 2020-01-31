package com.jandreasian.aroundme.homePage

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.jandreasian.aroundme.network.Posts

class HomePageViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _posts = MutableLiveData<MutableList<Posts>>()
    val posts: MutableLiveData<MutableList<Posts>>
        get() = _posts

    init {
        getAllPosts()
    }

    fun getAllPosts() {
        var postList : MutableList<Posts> = mutableListOf()
        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    postList.add(Posts(document.id, document.get("caption").toString(),document.get("imgSrcUrl").toString()))
                }
                _posts.value = postList
            }
            .addOnFailureListener { exception ->
                Log.w("HomePageViewModel", "Error getting documents.", exception)
            }
    }
}
