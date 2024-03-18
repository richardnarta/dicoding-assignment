package com.example.modul3_2.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.modul3_2.databinding.FragmentFollowingBinding
import com.example.modul3_2.ui.viewmodel.FollowingViewModel
import com.example.modul3_2.ui.UserAdapter

class FollowingFragment : Fragment() {
    private var _binding: FragmentFollowingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FollowingViewModel by viewModels()

    private lateinit var followingAdapter: UserAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingProgressBar: ContentLoadingProgressBar

    private lateinit var tvInfo: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowingBinding.inflate(inflater, container, false)

        binding.apply {
            recyclerView = rvUser
            tvInfo = tvNoFollower
            loadingProgressBar = progressBar
        }

        viewModel.apply {
            setUserName(DetailFragment.USER_NAME)
            loadFollowing()

            isLoading.observe(viewLifecycleOwner){
                if (it) loadingProgressBar.show() else loadingProgressBar.hide()
            }
        }

        createFollowingList()

        return binding.root
    }

    private fun createFollowingList() {
        followingAdapter = UserAdapter()

        recyclerView.apply {
            val rvLayoutManager = LinearLayoutManager(context)
            layoutManager = rvLayoutManager
            adapter = followingAdapter
            val itemDecoration = DividerItemDecoration(context, rvLayoutManager.orientation)
            addItemDecoration(itemDecoration)
        }

        viewModel.user.observe(viewLifecycleOwner){
            followingAdapter.submitList(it)
            tvInfo.isVisible = followingAdapter.itemCount == 0
        }
    }
}