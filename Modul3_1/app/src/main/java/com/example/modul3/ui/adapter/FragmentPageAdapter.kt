package com.example.modul3.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.modul3.ui.fragment.FollowerFragment
import com.example.modul3.ui.fragment.FollowingFragment

class FragmentPageAdapter (
    fragmentManager: FragmentManager,
    lifecycle: androidx.lifecycle.Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0){
            FollowerFragment()
        }else{
            FollowingFragment()
        }
    }
}