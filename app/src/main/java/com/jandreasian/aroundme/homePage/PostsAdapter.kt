package com.jandreasian.aroundme.homePage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jandreasian.aroundme.databinding.PostViewItemBinding
import com.jandreasian.aroundme.network.Posts

class PostsAdapter() : ListAdapter<Posts, PostsAdapter.PostsViewHolder>(DiffCallback) {

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        return PostsViewHolder(PostViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    class PostsViewHolder(private var binding: PostViewItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(posts: Posts) {
            binding.post = posts
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [Posts]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<Posts>() {
        override fun areItemsTheSame(oldItem: Posts, newItem: Posts): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Posts, newItem: Posts): Boolean {
            return oldItem.id == newItem.id
        }
    }
}