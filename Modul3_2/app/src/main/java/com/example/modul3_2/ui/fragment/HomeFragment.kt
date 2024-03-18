package com.example.modul3_2.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.core.widget.ContentLoadingProgressBar
import androidx.datastore.preferences.protobuf.Empty
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.modul3_2.R
import com.example.modul3_2.data.remote.response.UserResponse
import com.example.modul3_2.databinding.FragmentHomeBinding
import com.example.modul3_2.ui.MainActivity
import com.example.modul3_2.ui.UserAdapter
import com.example.modul3_2.ui.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels()

    private lateinit var loadingProgressBar: ContentLoadingProgressBar
    private lateinit var userAdapter: UserAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var tvNoUsers: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as MainActivity).supportActionBar!!.title = resources.getString(R.string.home_fragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        createUserList()
        createSearchView()
        itemOnClickListener()

        viewModel.apply {
            isLoading.observe(viewLifecycleOwner){
                if (it){
                    recyclerView.isVisible = false
                    loadingProgressBar.show()
                    tvNoUsers.isVisible = false
                }else{
                    loadingProgressBar.hide()
                    recyclerView.isVisible = true

                    user.observe(viewLifecycleOwner){ userList->
                        tvNoUsers.isVisible = userList!!.isEmpty()
                    }
                }
            }

            snackBarText.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let{text ->
                    Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

    private fun createUserList() {
        binding.apply {
            recyclerView = rvUser
            tvNoUsers = tvNoUser
            loadingProgressBar = progressBar
            searchView = svUser
        }

        userAdapter = UserAdapter()

        recyclerView.apply {
            val rvLayoutManager = LinearLayoutManager(context)
            layoutManager = rvLayoutManager
            adapter = userAdapter
            val itemDecoration = DividerItemDecoration(context, rvLayoutManager.orientation)
            addItemDecoration(itemDecoration)
        }

        viewModel.user.observe(viewLifecycleOwner){
            userAdapter.submitList(it)
        }
    }

    private fun createSearchView() {
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null){
                    searchView.clearFocus()
                    viewModel.setQuery(query)
                    viewModel.loadUser()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }

    private fun itemOnClickListener() {
        userAdapter.setOnClickListener(object :
            UserAdapter.OnClickListener {
            override fun onClick(position: Int, model: UserResponse) {
                findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToNavigationDetail(model.login!!))
            }
        })
    }
}