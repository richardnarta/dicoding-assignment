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
import com.example.modul3_2.databinding.FragmentFollowerBinding
import com.example.modul3_2.ui.viewmodel.FollowerViewModel
import com.example.modul3_2.ui.UserAdapter

class FollowerFragment : Fragment() {
    private var _binding: FragmentFollowerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FollowerViewModel by viewModels()

    private lateinit var followerAdapter: UserAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingProgressBar: ContentLoadingProgressBar

    private lateinit var tvInfo: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowerBinding.inflate(inflater, container, false)

        binding.apply {
            recyclerView = rvUser
            tvInfo = tvNoFollower
            loadingProgressBar = progressBar
        }

        viewModel.apply {
            setUserName(DetailFragment.USER_NAME)
            loadFollower()

            isLoading.observe(viewLifecycleOwner){
                if (it) loadingProgressBar.show() else loadingProgressBar.hide()
            }
        }

        createFollowerList()

        return binding.root
    }

    private fun createFollowerList() {
        followerAdapter = UserAdapter()

        recyclerView.apply {
            val rvLayoutManager = LinearLayoutManager(context)
            layoutManager = rvLayoutManager
            adapter = followerAdapter
            val itemDecoration = DividerItemDecoration(context, rvLayoutManager.orientation)
            addItemDecoration(itemDecoration)
        }

        viewModel.user.observe(viewLifecycleOwner){
            followerAdapter.submitList(it)
            tvInfo.isVisible = followerAdapter.itemCount == 0
        }
    }
}