package com.jandreasian.aroundme.homePage

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.jandreasian.aroundme.network.Posts

class HomePageViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _posts = MutableLiveData<MutableList<Posts>>()
    val posts: MutableLiveData<MutableList<Posts>>
        get() = _posts

    init {
        setUpListener()
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

    /**
     * This will setup the snapshot listener to add/update any elements in the RecyclerView.
     */
    fun setUpListener() {
        var postList : MutableList<Posts> = mutableListOf()
        val docRef = db.collection("posts")
        docRef.addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, e ->
            if (e != null) {
                Log.w("HomePageViewModel", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot?.getDocumentChanges() != null) {
                for (doc in snapshot!!.documentChanges) {
                    var post = Posts(doc.document.id, doc.document.get("caption").toString(), doc.document.get("imgSrcUrl").toString())

                    val a = postList.find { it.id == doc.document.id }
                    if(a === null) {
                        //Add new element in RecyclerView
                        postList.add(post)

                    } else {
                        //Update existing element in RecyclerView
                        postList?.find { it.id == doc.document.id }?.caption = doc.document.get("caption").toString()
                    }
                }

                _posts.value = postList
            } else {
                Log.d("HomePageViewModel", "Current data: null")
            }
        }

    }
}
