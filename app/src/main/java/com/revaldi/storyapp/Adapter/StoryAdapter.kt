package com.revaldi.storyapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.revaldi.storyapp.R
import com.bumptech.glide.Glide
import com.revaldi.storyapp.Models.StoriesData
import com.revaldi.storyapp.databinding.StoryItemBinding

class StoryAdapter : PagingDataAdapter<StoriesData, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyViewHolder(binding)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
                val data = getItem(position)
                if (data != null) {
                        holder.bind(data)
                }
        }

        inner class MyViewHolder(private val binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
                fun bind(data: StoriesData) {
                        Glide.with(binding.root.context)
                                .load(data.photoUrl)
                                .error(R.drawable.ic_error) // Gambar yang ditampilkan saat error
                                .centerCrop()
                                .into(binding.imageViewStory)
                        binding.textViewUsername.text = data.name
                        binding.textViewDescription.text = data.createdAt

                        binding.root.setOnClickListener {
                                onItemCallBack?.onItemClicked(data)
                        }
                }
        }

        interface OnItemClickCallBack {
                fun onItemClicked(data: StoriesData)
        }

        private var onItemCallBack: OnItemClickCallBack? = null

        fun setOnClickCallback(onItemCallBack: OnItemClickCallBack) {
                this.onItemCallBack = onItemCallBack
        }



        companion object {
                 val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoriesData>() {
                        override fun areItemsTheSame(oldItem: StoriesData, newItem: StoriesData): Boolean {
                                return oldItem.id == newItem.id
                        }

                        override fun areContentsTheSame(oldItem: StoriesData, newItem: StoriesData): Boolean {
                                return oldItem == newItem
                        }
                }
        }

}