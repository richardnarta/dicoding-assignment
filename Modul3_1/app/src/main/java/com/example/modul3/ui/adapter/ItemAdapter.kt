package com.example.modul3.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.modul3.data.response.UserResponse
import com.example.modul3.databinding.RvItemBinding

class ItemAdapter: ListAdapter<UserResponse, ItemAdapter.MyViewHolder>(diffCallback){
    private var onClickListener: OnClickListener? = null

    inner class MyViewHolder(binding: RvItemBinding): RecyclerView.ViewHolder(binding.root) {
        val image = binding.circleImage
        val tvUsername = binding.tvUsername
    }

    companion object{
        val diffCallback = object: DiffUtil.ItemCallback<UserResponse>(){
            override fun areItemsTheSame(oldItem: UserResponse, newItem: UserResponse): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: UserResponse, newItem: UserResponse): Boolean {
                return oldItem.login == newItem.login
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)

        holder.apply {
            Glide.with(holder.itemView.context)
                .load(item?.avatarUrl)
                .centerInside().into(image)

            tvUsername.text = item?.login
        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null){
                onClickListener!!.onClick(position, item)
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int, model: UserResponse)
    }
}