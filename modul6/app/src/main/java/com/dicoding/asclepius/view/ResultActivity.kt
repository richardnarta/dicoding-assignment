package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.entity.ResultEntity
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.viewmodel.ResultViewmodel
import com.dicoding.asclepius.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var viewModel: ResultViewmodel

    companion object{
        const val RESULT = "result"
    }

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var progressBar: ContentLoadingProgressBar
    private lateinit var ivImagePreview: ImageView
    private lateinit var tvResult: TextView
    private lateinit var tvScore: TextView

    private lateinit var resultEntity: ResultEntity
    private var isAdded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar!!.title = resources.getString(R.string.result)

        binding.apply {
            newsRecyclerView = rvNews
            progressBar = newsProgressBar
            ivImagePreview = resultImage
            tvResult = resultText
            tvScore = scoreResultText
        }

        viewModel = obtainViewModel(this@ResultActivity)

        showResults()
        showNews()
        newsOnClickListener()

        viewModel.snackBarText.observe(this){
            it.getContentIfNotHandled()?.let{text ->
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun newsOnClickListener() {
        newsAdapter.setOnClickListener(object :
        NewsAdapter.OnClickListener{
            override fun onClick(position: Int, model: ArticlesItem) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(model.url))

                startActivity(browserIntent)
            }
        })
    }

    private fun showNews() {
        newsAdapter = NewsAdapter()

        newsRecyclerView.apply {
            adapter = newsAdapter

            val rvLayoutManager = LinearLayoutManager(this@ResultActivity)
            layoutManager = rvLayoutManager

            val itemDecoration = DividerItemDecoration(this@ResultActivity, rvLayoutManager.orientation)
            addItemDecoration(itemDecoration)
        }

        viewModel.apply {
            news.observe(this@ResultActivity){newsArticle ->
                newsAdapter.submitList(newsArticle)

            }

            isLoading.observe(this@ResultActivity){
                if (it){
                    newsRecyclerView.isVisible = false
                    progressBar.show()
                }else{
                    newsRecyclerView.isVisible = true
                    progressBar.hide()
                }
            }
        }
    }

    private fun showResults() {
        val result = intent.getStringArrayListExtra(RESULT)

        if (result != null){
            Glide.with(this)
                .load(result[0].toUri()).centerInside()
                .into(ivImagePreview)
            tvResult.text = resources.getString(R.string.category_result, result[1])
            tvScore.text = resources.getString(R.string.confidence_score, result[2])

            resultEntity = ResultEntity(image = result[0],
                result = result[1],
                score = result[2],
                timeStamp = result[3])
        }

        viewModel.apply {
            checkHistory(result?.get(3)!!){exist ->
                isAdded = exist == 1
            }
        }
    }

    private fun saveHistory() {
        val result = intent.getStringArrayListExtra(RESULT)

        viewModel.apply {
            isAdded = if (isAdded) {
                delete(result?.get(3)!!)
                false
            }else {
                insert(resultEntity)
                true
            }
        }
    }

    private fun updateMenuItem(item: MenuItem?) {
        if (!isAdded) {
            item?.setIcon(R.drawable.bookmark_border)
        }else {
            item?.setIcon(R.drawable.bookmark)
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): ResultViewmodel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[ResultViewmodel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.result_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val menuItem = menu?.findItem(R.id.bookmark)
        updateMenuItem(menuItem)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.bookmark -> {
                saveHistory()
                updateMenuItem(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}