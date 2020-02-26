package com.jandreasian.aroundme.homePage

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.jandreasian.aroundme.R
import com.jandreasian.aroundme.databinding.HomePageFragmentBinding
import com.jandreasian.aroundme.network.GpsUtils
import com.jandreasian.aroundme.network.Posts

const val LOCATION_REQUEST = 100
const val GPS_REQUEST = 101

class HomePageFragment : Fragment() {

    private val IMAGE_CAPTURE_CODE = 1001

    private val PERMISSION_CODE = 1000

    private var isGPSEnabled = false

    private lateinit var image_uri: Uri

    private lateinit var viewModel: HomePageViewModel

    private lateinit var binding: HomePageFragmentBinding

    private val adapter = PostsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        GpsUtils(requireContext()).turnGPSOn(object : GpsUtils.OnGpsListener {

            override fun gpsStatus(isGPSEnable: Boolean) {
                this@HomePageFragment.isGPSEnabled = isGPSEnable
            }
        })

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.home_page_fragment,
            container,
            false
        )

        viewModel = ViewModelProviders.of(this)
            .get(HomePageViewModel::class.java)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.setLifecycleOwner(this)

        // Giving the binding access to the ViewModel
        binding.viewModel = viewModel

        binding.photosGrid.adapter = adapter

        setHasOptionsMenu(true)

        invokeLocationAction()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()

        if (id == R.id.action_add) {
            checkPermissions()
        }
        if (id == R.id.action_refresh) {
            adapter.notifyDataSetChanged()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun isPermissionsGranted() =
        checkSelfPermission(requireContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED
                && checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED

    private fun isLocationPermissionGranted() =
        checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun checkPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(isPermissionsGranted()) {
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                requestPermissions(permission, PERMISSION_CODE)
            } else {
                //Permission already granted
                openCamera()
            }

        } else {
            //System is less than marshmallow
            openCamera()
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!

        //Camera Intent
        var cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSION_CODE -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    //Permission Denied
                }
            }
            LOCATION_REQUEST -> {
                invokeLocationAction()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK) {
            val post = Posts("test","test",image_uri.toString())
            findNavController().navigate(HomePageFragmentDirections.actionHomePageFragmentToNewPostFragment(post))
        }
    }

    private fun invokeLocationAction() {
        when {
            isLocationPermissionGranted() -> startLocationUpdate()

            else -> ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST
            )
        }
    }

    private fun startLocationUpdate() {
        viewModel.getLocationData().observe(this, Observer {
            Log.d("HomePageFragment", getString(R.string.latLong, it.longitude, it.latitude))
        })
    }
}
