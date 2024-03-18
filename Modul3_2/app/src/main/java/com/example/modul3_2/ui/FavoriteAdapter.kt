package com.example.modul3_2.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.modul3_2.R
import com.example.modul3_2.data.local.entity.UserEntity
import com.example.modul3_2.databinding.ItemHomeBinding
import com.example.modul3_2.utils.FavUserDiffCallback

class FavoriteAdapter: RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>() {
    private var onClickListener: OnClickListener? = null

    private val listFavUsers = ArrayList<UserEntity>()
    fun setListFavUsers(listUsers: List<UserEntity>){
        val diffCallback = FavUserDiffCallback(this.listFavUsers, listUsers)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listFavUsers.clear()
        this.listFavUsers.addAll(listUsers)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class MyViewHolder(private val binding: ItemHomeBinding) : RecyclerView.ViewHolder(binding.root){
        val image = binding.circleImage
        val tvUsername = binding.tvUsername
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listFavUsers.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = listFavUsers[position]

        holder.apply {
            Glide.with(holder.itemView.context)
                .load(user.avatarUrl).error(R.drawable.profile)
                .centerInside().into(image)

            tvUsername.text = user.userName
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
        fun onClick(position: Int, model: UserEntity)
    }
}