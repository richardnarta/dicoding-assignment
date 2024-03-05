package com.example.modul3.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.activity.viewModels
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.modul3.R
import com.example.modul3.data.response.UserResponse
import com.example.modul3.databinding.ActivityMainBinding
import com.example.modul3.ui.adapter.ItemAdapter
import com.example.modul3.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel>()

    private lateinit var adapter:ItemAdapter
    private lateinit var searchView:SearchView
    private lateinit var recyclerView:RecyclerView
    private lateinit var progressBar: ContentLoadingProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createUserList()

        createSearchView()

        itemOnClickListener()

        progressBar = binding.progressBar
        mainViewModel.isLoading.observe(this){
            if (it) progressBar.show() else progressBar.hide()
        }

        mainViewModel.snackBarText.observe(this) {
            it.getContentIfNotHandled()?.let { text ->
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun itemOnClickListener() {
        adapter.setOnClickListener(object :
        ItemAdapter.OnClickListener{
            override fun onClick(position: Int, model: UserResponse) {
                val moveToDetail = Intent(this@MainActivity, DetailActivity::class.java)
                moveToDetail.putExtra(DetailActivity.USER_NAME, model.login)
                startActivity(moveToDetail)
            }
        })
    }

    private fun createSearchView() {
        searchView = binding.svUser
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null){
                    searchView.clearFocus()
                    mainViewModel.setQuery(query)
                    mainViewModel.loadUser()
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }

    private fun createUserList(){
        recyclerView = binding.rvUser

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        recyclerView.addItemDecoration(itemDecoration)

        adapter = ItemAdapter()
        recyclerView.adapter = adapter

        mainViewModel.user.observe(this){
            adapter.submitList(it)
        }
    }
}