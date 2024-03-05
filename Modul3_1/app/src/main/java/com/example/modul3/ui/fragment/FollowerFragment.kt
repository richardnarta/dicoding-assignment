package com.example.modul3.ui.fragment

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
import com.example.modul3.databinding.FragmentFollowerBinding
import com.example.modul3.ui.DetailActivity
import com.example.modul3.ui.adapter.ItemAdapter
import com.example.modul3.viewmodel.FollowerViewModel

class FollowerFragment : Fragment() {
    private var _binding: FragmentFollowerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FollowerViewModel by viewModels()

    private lateinit var adapter:ItemAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ContentLoadingProgressBar

    private lateinit var tvInfo: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowerBinding.inflate(inflater, container, false)

        tvInfo = binding.tvNoFollower

        val username = (activity as DetailActivity).getUsername()
        viewModel.setUserName(username)
        viewModel.loadFollower()

        createFollowerList()

        progressBar = binding.progressBar
        viewModel.isLoading.observe(viewLifecycleOwner){
            if (it) progressBar.show() else progressBar.hide()
        }

        return binding.root
    }

    private fun createFollowerList() {
        recyclerView = binding.rvUser
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        recyclerView.addItemDecoration(itemDecoration)

        adapter = ItemAdapter()
        recyclerView.adapter = adapter

        viewModel.user.observe(viewLifecycleOwner){
            adapter.submitList(it)
            tvInfo.isVisible = adapter.itemCount == 0
        }
    }
}