package com.jandreasian.aroundme.newPost

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.jandreasian.aroundme.network.Posts
import org.imperiumlabs.geofirestore.GeoFirestore
import org.imperiumlabs.geofirestore.extension.setLocation

class NewPostViewModel(post: Posts, app: Application) : AndroidViewModel(app) {

    private val db = FirebaseFirestore.getInstance().collection("posts")
    private val geoFirestore = GeoFirestore(db)
    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(app)

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

        //Get Last Location
        fusedLocationClient!!.lastLocation
            .addOnSuccessListener { location ->
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
                db.document(post.id).set(post)

                //Create a Location Data in Collection for the New Post that is created
                geoFirestore.setLocation(post.id, GeoPoint(location.latitude, location.longitude)) { e ->
                    if (e == null) {
                        Log.d("NewPage", "Location saved on server successfully!")
                    }
                    else {
                        Log.d("NewPage", "An error has occurred: $e")
                    }
                }

                onHomePage()
            }
    }

    private fun onHomePage() {
        _eventNewPostUploaded.value = true
    }

    fun onHomePageComplete() {
        _eventNewPostUploaded.value = false
    }

}
