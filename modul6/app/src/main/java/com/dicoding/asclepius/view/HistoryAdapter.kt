package com.dicoding.asclepius.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.entity.ResultEntity
import com.dicoding.asclepius.databinding.HistoryItemBinding

class HistoryAdapter: ListAdapter<ResultEntity, HistoryAdapter.MyViewHolder>(diffCallback) {
    private var onClickListener: OnClickListener? = null

    inner class MyViewHolder(binding: HistoryItemBinding): RecyclerView.ViewHolder(binding.root){
        val image = binding.ivHistory
        val resultHistory = binding.tvResult
        val timeStamp = binding.tvTimeStamp
    }

    companion object{
        val diffCallback = object: DiffUtil.ItemCallback<ResultEntity>(){
            override fun areItemsTheSame(oldItem: ResultEntity, newItem: ResultEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ResultEntity, newItem: ResultEntity): Boolean {
                return oldItem.timeStamp == newItem.timeStamp
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = getItem(position)

        holder.apply {
            Glide.with(holder.itemView.context)
                .load(currentItem?.image?.toUri())
                .centerInside().into(image)

            resultHistory.text = itemView.context.resources.getString(R.string.history_result,
                currentItem?.result, currentItem?.score)

            timeStamp.text = currentItem?.timeStamp
        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null){
                onClickListener!!.onClick(position, currentItem)
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int, model: ResultEntity)
    }
}