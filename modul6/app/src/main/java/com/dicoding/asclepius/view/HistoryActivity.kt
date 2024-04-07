package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.entity.ResultEntity
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.viewmodel.HistoryViewmodel
import com.dicoding.asclepius.viewmodel.ViewModelFactory

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding

    private lateinit var viewModel: HistoryViewmodel

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var noHistory: TextView

    private var classificationResult = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar!!.title = resources.getString(R.string.history)

        viewModel = obtainViewModel(this)

        binding.apply {
            historyRecyclerView = rvHistory
            noHistory = tvNoHistory
        }

        loadHistory()
        historyOnClickListener()
    }

    private fun historyOnClickListener() {
        historyAdapter.setOnClickListener(object :
        HistoryAdapter.OnClickListener{
            override fun onClick(position: Int, model: ResultEntity) {
                model.image?.let { classificationResult.add(it) }
                model.result?.let { classificationResult.add(it) }
                model.score?.let { classificationResult.add(it) }
                model.timeStamp?.let { classificationResult.add(it) }

                moveToResult()
            }
        })
    }

    private fun loadHistory() {
        historyAdapter = HistoryAdapter()

        historyRecyclerView.apply {
            val rvLayoutManager = LinearLayoutManager(context)
            layoutManager = rvLayoutManager

            adapter = historyAdapter

            val itemDecoration = DividerItemDecoration(context, rvLayoutManager.orientation)
            addItemDecoration(itemDecoration)
        }

        viewModel.getAllHistory().observe(this@HistoryActivity) {
            if (it != null) historyAdapter.submitList(it)

            historyRecyclerView.isVisible = historyAdapter.itemCount != 0
            noHistory.isVisible = historyAdapter.itemCount == 0
        }
    }

    private fun moveToResult() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.RESULT, classificationResult)
        startActivity(intent)
    }

    private fun obtainViewModel(activity: AppCompatActivity): HistoryViewmodel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[HistoryViewmodel::class.java]
    }
}