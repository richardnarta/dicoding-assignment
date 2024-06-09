package com.example.modul7.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.modul7.R
import com.example.modul7.databinding.ActivityMainBinding
import com.example.modul7.model.local.datastore.LoginSession
import com.example.modul7.model.local.datastore.dataStore
import com.example.modul7.ui.AddStoryActivity.Companion.UPLOAD_RESULT
import com.example.modul7.utils.Event
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

    private var isUploadSuccess = false

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

        checkBundle()

        swipeLayout.setOnRefreshListener(this)

        fabAdd.setOnClickListener {
            moveToActivity(AddStoryActivity::class.java)
        }
    }

    private fun displayStoryList() {
        storyAdapter = StoryAdapter()
        storyRecyclerView.setHasFixedSize(true)

        storyRecyclerView.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )

        storyAdapter.addLoadStateListener {
            if (it.refresh is LoadState.Loading) {
                progressBar.show()
                storyRecyclerView.isVisible = false
            } else if (it.hasError) {
                lifecycleScope.launch {
                    if (pref.getLoginInfo().first()) {
                        viewModel.snackBarText.value = Event(resources.getString(R.string.error_load_story_list))
                        showToast()
                        progressBar.hide()
                        storyRecyclerView.isVisible = true
                    }
                }
            } else {
                progressBar.hide()
                storyRecyclerView.isVisible = true

                if (isUploadSuccess) {
                    storyRecyclerView.smoothScrollToPosition(0)
                }

                isUploadSuccess = false
            }
        }

        viewModel.storyList.observe(this) {
            storyAdapter.submitData(lifecycle, it)
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

    private fun checkBundle() {
        isUploadSuccess = intent.getBooleanExtra(UPLOAD_RESULT, false)
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

            R.id.map_icon -> {
                progressBar.show()
                moveToActivity(MapsActivity::class.java)
                progressBar.hide()
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
        viewModel.refresh()
        swipeLayout.isRefreshing = false
    }
}