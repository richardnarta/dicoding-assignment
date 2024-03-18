package com.example.modul3_2.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.modul3_2.R
import com.example.modul3_2.data.local.entity.UserEntity
import com.example.modul3_2.databinding.FragmentFavoriteBinding
import com.example.modul3_2.ui.FavoriteAdapter
import com.example.modul3_2.ui.MainActivity
import com.example.modul3_2.ui.viewmodel.FavoriteViewModel
import com.example.modul3_2.utils.ViewModelFactory

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding ?= null
    private val binding get() = _binding!!

    private lateinit var viewModel: FavoriteViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: FavoriteAdapter
    private lateinit var tvNoFav: TextView
    private lateinit var loadingProgressBar: ContentLoadingProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as MainActivity).supportActionBar!!.title = resources.getString(R.string.favorite_fragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)

        val factory = ViewModelFactory.getInstance(this.requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]

        createUserList()
        itemOnClickListener()

        return binding.root
    }

    private fun createUserList() {
        binding.apply {
            recyclerView = rvUser
            tvNoFav = tvNoUser
            loadingProgressBar = progressBar
        }

        userAdapter = FavoriteAdapter()

        recyclerView.apply {
            val rvLayoutManager = LinearLayoutManager(context)
            layoutManager = rvLayoutManager
            adapter = userAdapter
            val itemDecoration = DividerItemDecoration(context, rvLayoutManager.orientation)
            addItemDecoration(itemDecoration)
        }

        viewModel.getAllFavoriteUser().observe(viewLifecycleOwner){
            if (it != null) userAdapter.setListFavUsers(it)
            tvNoFav.isVisible = userAdapter.itemCount == 0
        }
    }

    private fun itemOnClickListener() {
        userAdapter.setOnClickListener(object :
        FavoriteAdapter.OnClickListener{
            override fun onClick(position: Int, model: UserEntity) {
                findNavController().navigate(FavoriteFragmentDirections.actionNavigationFavoriteToNavigationDetail(model.userName!!))
            }
        })
    }
}