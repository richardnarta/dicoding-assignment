package com.example.modul7.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.modul7.R
import com.example.modul7.databinding.ActivityMainBinding
import com.example.modul7.model.local.datastore.LoginSession
import com.example.modul7.model.local.datastore.dataStore
import com.example.modul7.utils.Event
import com.example.modul7.utils.StoryResult
import com.example.modul7.viewmodel.StoryViewModel
import com.example.modul7.viewmodel.StoryViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: ActivityMainBinding

    private lateinit var isLoading: MutableLiveData<Boolean>

    private lateinit var factory: StoryViewModelFactory
    private lateinit var viewModel: StoryViewModel

    private lateinit var pref: LoginSession

    private lateinit var storyRecyclerView: RecyclerView
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var progressBar: ContentLoadingProgressBar
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var swipeLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.title = resources.getString(R.string.main_activity_title)

        isLoading = MutableLiveData(true)

        installSplashScreen().apply {
            setKeepOnScreenCondition{
                isLoading.value!!
            }
        }

        pref = LoginSession.getInstance(application.dataStore)

        binding.apply {
            storyRecyclerView = rvStory
            progressBar = progressBarHome
            fabAdd = fabAddStory
            swipeLayout = swipeRefreshLayout
        }

        checkLoginStatus()

        swipeLayout.setOnRefreshListener(this)

        fabAdd.setOnClickListener {
            moveToActivity(AddStoryActivity::class.java)
        }
    }

    private fun displayStoryList() {
        storyAdapter = StoryAdapter()
        storyRecyclerView.adapter = storyAdapter

        viewModel.showStoryList().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is StoryResult.Loading -> progressBar.show()

                    is StoryResult.Success -> {
                        progressBar.hide()
                        storyAdapter.submitList(result.data)
                    }

                    is StoryResult.Error -> {
                        progressBar.hide()
                        viewModel.snackBarText.value = Event(resources.getString(R.string.error_toast, result.error))
                        showToast()
                    }
                }
            }
        }
    }

    private fun checkLoginStatus() {
        lifecycleScope.launch {
            if (!pref.getLoginInfo().first()) {
                moveToActivity(LoginActivity::class.java, true)
                finish()
            } else {
                factory = StoryViewModelFactory.getInstance(this@MainActivity)
                viewModel = ViewModelProvider(this@MainActivity, factory)[StoryViewModel::class.java]
                displayStoryList()
            }
            isLoading.value = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.log_out -> {
                lifecycleScope.launch {
                    pref.setLoginSession(false, "")
                    moveToActivity(LoginActivity::class.java, true)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun moveToActivity(cls: Class<*>, clear: Boolean = false) {
        val move = Intent(this, cls)
        if (clear) {
            move.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(move)
    }

    private fun showToast() {
        viewModel.snackBarText.observe(this) {
            it.getContentIfNotHandled()?.let { text ->
                Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRefresh() {
        displayStoryList()
        swipeLayout.isRefreshing = false
    }
}