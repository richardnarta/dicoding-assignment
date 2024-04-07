package com.dicoding.asclepius.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.databinding.NewsItemBinding

class NewsAdapter: ListAdapter<ArticlesItem, NewsAdapter.MyViewHolder>(diffCallback) {
    private var onClickListener: OnClickListener? = null

    companion object{
        val diffCallback = object: DiffUtil.ItemCallback<ArticlesItem>(){
            override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem.url == newItem.url
            }
        }
    }

    inner class MyViewHolder(binding: NewsItemBinding): RecyclerView.ViewHolder(binding.root){
        val image = binding.ivNews
        val title = binding.tvTitle
        val description = binding.tvDesc
        val publishedDate = binding.tvDate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val newsItem = getItem(position)

        holder.apply {
            Glide.with(holder.itemView.context)
                .load(newsItem?.urlToImage).centerInside().error(R.drawable.img)
                .into(image)

            title.text = newsItem?.title
            description.text = newsItem?.description
            publishedDate.text = holder.itemView.context.resources.getString(R.string.publish_date,
                newsItem?.publishedAt?.take(10) ?: "-"
            )
        }

        holder.itemView.setOnClickListener{
            if (onClickListener != null){
                onClickListener!!.onClick(position, newsItem)
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int, model: ArticlesItem)
    }
}