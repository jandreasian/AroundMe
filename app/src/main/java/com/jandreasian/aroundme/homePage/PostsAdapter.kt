package com.jandreasian.aroundme.homePage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jandreasian.aroundme.R
import com.jandreasian.aroundme.network.Posts

class PostsAdapter: RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    var data =  listOf<Posts>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.caption.text = item.caption
        Glide.with(holder.itemView.getContext()).load(item.imgSrcUrl).into(holder.qualityImage);

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.post_view_item, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val caption: TextView = itemView.findViewById(R.id.caption)
        val qualityImage: ImageView = itemView.findViewById(R.id.post_image)
    }

}