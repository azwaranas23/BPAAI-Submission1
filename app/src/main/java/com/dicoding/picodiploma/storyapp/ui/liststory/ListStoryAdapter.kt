package com.dicoding.picodiploma.storyapp.ui.liststory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dicoding.picodiploma.storyapp.network.StoryItem
import com.dicoding.picodiploma.storyapp.databinding.ItemRowStoryBinding

class ListStoryAdapter(private val listStory: List<StoryItem>) :
    RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listStory[position])
    }

    override fun getItemCount(): Int = listStory.size

    inner class ListViewHolder(private var binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryItem) {

            binding.pbItemStory.visibility = View.GONE

            binding.imgItemStory.transitionName = story.photoUrl

            Glide.with(binding.imgItemStory.context)
                .load(story.photoUrl)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                .into(binding.imgItemStory)

            binding.tvItemName.text = story.name

            binding.cvItem.setOnClickListener { onItemClickCallback.onItemClicked(story, binding) }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(story: StoryItem, binding: ItemRowStoryBinding)
    }
}