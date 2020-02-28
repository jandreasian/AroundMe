package com.jandreasian.aroundme.homePage

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.MetadataChanges
import com.jandreasian.aroundme.network.LocationService
import com.jandreasian.aroundme.network.Posts
import org.imperiumlabs.geofirestore.GeoFirestore
import org.imperiumlabs.geofirestore.listeners.GeoQueryEventListener
import com.google.android.gms.location.LocationServices


class HomePageViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance().collection("posts")
    private val geoFirestore = GeoFirestore(db)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

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
    private fun setUpListener() {
        var postList : MutableList<Posts> = mutableListOf()

        fusedLocationClient!!.lastLocation
            .addOnSuccessListener { location ->
                //This will create a GeoQuery based on the current location with a radius of 0.1 kilometers.
                val geoQuery = geoFirestore.queryAtLocation(GeoPoint(location.latitude, location.longitude), 0.1)
                geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
                    override fun onGeoQueryError(exception: Exception) {
                        Log.i("HomePageViewModel", "onGeoQueryError")
                    }

                    //This will add only posts that are within the area
                    override fun onKeyEntered(documentID: String, location: GeoPoint) {
                        db.document(documentID).get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    Log.d("HomePageViewModel", "DocumentSnapshot data: ${document.get("caption")}")
                                    var post = Posts(document.id, document.get("caption").toString(), document.get("imgSrcUrl").toString())
                                    postList.add(post)
                                } else {
                                    Log.d("HomePageViewModel", "No such document")
                                }
                            }
                            .addOnFailureListener { exception ->
                            }
                    }

                    override fun onKeyMoved(documentID: String, location: GeoPoint) {
                        Log.i("HomePageViewModel", "onKeyMoved")
                    }

                    //Remove the post if it is not within the range
                    override fun onKeyExited(key: String) {
                        Log.i("HomePageViewModel", String.format("Provider %s is no longer in the search area", key))
                        var postToBeRemoved = postList.find { it.id == key }
                        postList.remove(postToBeRemoved)
                    }

                    override fun onGeoQueryReady() {
                        Log.i("HomePageViewModel", "onGeoQueryReady")
                        _posts.value = postList
                    }
                })
            }

        db.addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, e ->
            if (e != null) {
                Log.w("HomePageViewModel", "Listen failed.", e)
                return@addSnapshotListener
            }

            //This gets any updates that happen to the posts
            if (snapshot?.getDocumentChanges() != null) {
                for (doc in snapshot!!.documentChanges) {
                    postList?.find { it.id == doc.document.id }?.caption = doc.document.get("caption").toString()
                }
            } else {
                Log.d("HomePageViewModel", "Current data: null")
            }
        }
    }
}
