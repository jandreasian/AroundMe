package com.jandreasian.aroundme.homePage

import android.content.res.Configuration
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
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

import com.jandreasian.aroundme.R
import com.jandreasian.aroundme.databinding.HomePageFragmentBinding
import com.jandreasian.aroundme.network.Posts
import kotlinx.android.synthetic.main.home_page_fragment.*

class HomePageFragment : Fragment() {

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

        return binding.root
    }
}
