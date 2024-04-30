package com.example.modul7.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import com.bumptech.glide.Glide
import com.example.modul7.R
import com.example.modul7.databinding.ActivityDetailBinding
import com.example.modul7.utils.Event
import com.example.modul7.utils.StoryResult
import com.example.modul7.viewmodel.StoryViewModel
import com.example.modul7.viewmodel.StoryViewModelFactory

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    private val factory: StoryViewModelFactory = StoryViewModelFactory.getInstance(this)
    private val viewModel: StoryViewModel by viewModels {
        factory
    }

    private lateinit var ivDetail: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvDesc: TextView
    private lateinit var progressBar: ContentLoadingProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.title = resources.getString(R.string.detail_title)

        binding.apply {
            ivDetail = ivDetailPhoto
            tvName = tvDetailName
            tvDesc = tvDetailDescription
            progressBar = progressBarDetail
        }

        displayStoryDetail()
    }

    private fun displayStoryDetail() {
        val idStory = intent.getStringArrayListExtra(STORY)!!

        viewModel.getStoryDetail(idStory[0].toString()).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is StoryResult.Loading -> progressBar.show()

                    is StoryResult.Success -> {
                        progressBar.hide()

                        Glide.with(this).load(idStory[1].toString())
                            .dontTransform().dontAnimate()
                            .into(ivDetail)

                        tvName.text = result.data.name
                        tvDesc.text = result.data.description
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

    private fun showToast() {
        viewModel.snackBarText.observe(this) {
            it.getContentIfNotHandled()?.let { text ->
                Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val STORY = "story"
    }
}