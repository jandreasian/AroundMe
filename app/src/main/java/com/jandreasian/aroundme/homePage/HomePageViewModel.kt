package com.jandreasian.aroundme.homePage

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.jandreasian.aroundme.network.LocationService
import com.jandreasian.aroundme.network.Posts

class HomePageViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance()

    private val _posts = MutableLiveData<MutableList<Posts>>()
    val posts: MutableLiveData<MutableList<Posts>>
        get() = _posts

    private val locationData = LocationService(application)


    init {
        setUpListener()
    }

    fun getLocationData() = locationData

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
