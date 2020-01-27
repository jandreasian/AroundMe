package com.jandreasian.aroundme.newPost

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.jandreasian.aroundme.R
import com.jandreasian.aroundme.databinding.NewPostFragmentBinding
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.Glide
import com.jandreasian.aroundme.network.Posts

class NewPostFragment : Fragment() {


    private lateinit var viewModel: NewPostViewModel
    private lateinit var viewModelFactory: NewPostViewModelFactory
    private lateinit var binding: NewPostFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val application = requireNotNull(activity).application

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.new_post_fragment,
            container,
            false
        )

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        //binding.setLifecycleOwner(this)

        val args = NewPostFragmentArgs.fromBundle(arguments!!).imageURI
        Log.d("NewPostFragment", args.toString())
        viewModelFactory = NewPostViewModelFactory(args, application)

        viewModel = ViewModelProviders.of(
           this, viewModelFactory).get(NewPostViewModel::class.java)

        // Add observer for score
        viewModel.selectedImage.observe(this, Observer { imageSrcUri ->
            Glide
                .with(requireContext())
                .load(imageSrcUri)
                .apply(RequestOptions().override(600, 200))
                .into(binding.imageView)
        })

        binding.button.setOnClickListener{
            viewModel.newPost(args, Posts(binding.plainTextInput.text.toString(), ""))
        }

        viewModel.eventNewPostUploaded.observe(this, Observer { newPost ->
            if(newPost) {
                findNavController().navigate(NewPostFragmentDirections.actionNewPostFragmentToHomePageFragment())
                viewModel.onHomePageComplete()
            }

        })

        return binding.root
    }
}
