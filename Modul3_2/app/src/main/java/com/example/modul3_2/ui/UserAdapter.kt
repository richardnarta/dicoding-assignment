package com.example.modul3_2.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.modul3_2.R
import com.example.modul3_2.data.remote.response.UserResponse
import com.example.modul3_2.databinding.ItemHomeBinding

class UserAdapter: ListAdapter<UserResponse, UserAdapter.MyViewHolder>(diffCallback) {
    private var onClickListener: OnClickListener? = null

    inner class MyViewHolder(binding: ItemHomeBinding): RecyclerView.ViewHolder(binding.root) {
        val image = binding.circleImage
        val tvUsername = binding.tvUsername
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)

        holder.apply {
            Glide.with(holder.itemView.context)
                .load(user?.avatarUrl).error(R.drawable.profile)
                .centerInside().into(image)

            tvUsername.text = user?.login
        }

        holder.itemView.setOnClickListener{
            if (onClickListener != null){
                onClickListener!!.onClick(position, user)
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int, model: UserResponse)
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
}