package com.jandreasian.aroundme.homePage

import android.Manifest
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
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController

import com.jandreasian.aroundme.R
import com.jandreasian.aroundme.databinding.HomePageFragmentBinding

class HomePageFragment : Fragment() {

    private val IMAGE_CAPTURE_CODE = 1001

    private val PERMISSION_CODE = 1000

    lateinit var image_uri: Uri

    private lateinit var viewModel: HomePageViewModel

    private lateinit var binding: HomePageFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

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

        val adapter = PostsAdapter()
        binding.photosGrid.adapter = adapter

        viewModel.getAllPosts().observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.data = it
            }
        })

        /*viewModel.navigateToNewPost.observe(this, Observer {
            if ( null != it ) {
                // Must find the NavController from the Fragment
                this.findNavController().navigate(R.id.action_homePageFragment_to_newPostFragment)
                // Tell the ViewModel we've made the navigate call to prevent multiple navigation
                viewModel.createNewPostComplete()
            }
        })*/

        setHasOptionsMenu(true)
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
            viewModel.getAllPosts()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun isPermissionsGranted() =
        checkSelfPermission(requireContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED
                || checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED

    private fun checkPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(isPermissionsGranted()) {
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSION_CODE -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    //Permission Denied
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK) {
            Log.d("HomePageFragment", "Image URI: " + image_uri.toString())
            //viewModel.newPost(image_uri)
            //viewModel.createNewPost(image_uri)
            findNavController().navigate(HomePageFragmentDirections.actionHomePageFragmentToNewPostFragment(image_uri))

        }
    }
}
