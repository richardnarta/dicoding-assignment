package com.example.modul7.ui

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.modul7.databinding.StoryItemBinding
import com.example.modul7.model.remote.response.ListStoryItem
import com.example.modul7.ui.DetailActivity.Companion.STORY

class StoryAdapter: ListAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)

        holder.apply {
            Glide.with(holder.itemView.context)
                .load(story.photoUrl)
                .dontTransform().dontAnimate()
                .into(image)

            userName.text = story.name
            desc.text = story.description

            itemView.setOnClickListener {
                val move = Intent(itemView.context, DetailActivity::class.java)
                move.putExtra(STORY, arrayListOf(story.id, story.photoUrl))

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(image, "detail_photo"),
                        Pair(userName, "detail_name"),
                        Pair(desc, "detail_desc")
                    )

                itemView.context.startActivity(move, optionsCompat.toBundle())
            }
        }
    }

    inner class MyViewHolder (binding: StoryItemBinding): RecyclerView.ViewHolder(binding.root) {
        val image = binding.ivItemPhoto
        val userName = binding.tvItemName
        val desc = binding.tvItemDescription
    }

    companion object{
        val diffCallback = object: DiffUtil.ItemCallback<ListStoryItem>(){
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id== newItem.id
            }
        }
    }
}