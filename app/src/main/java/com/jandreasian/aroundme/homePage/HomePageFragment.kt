package com.jandreasian.aroundme.homePage

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer

import com.jandreasian.aroundme.R
import com.jandreasian.aroundme.databinding.HomePageFragmentBinding

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
            viewModel.newPost()
        }

        return super.onOptionsItemSelected(item)
    }
}
