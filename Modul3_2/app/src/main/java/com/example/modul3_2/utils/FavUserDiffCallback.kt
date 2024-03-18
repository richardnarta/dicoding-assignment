package com.example.modul3_2.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.modul3_2.data.local.entity.UserEntity

class FavUserDiffCallback(private val oldUserList: List<UserEntity>,
    private val newUserList: List<UserEntity>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldUserList.size

    override fun getNewListSize(): Int = newUserList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldUserList[oldItemPosition].userName == newUserList[newItemPosition].userName
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldUserList[oldItemPosition]
        val newUser = newUserList[newItemPosition]

        return oldUser.avatarUrl == newUser.avatarUrl
    }

}