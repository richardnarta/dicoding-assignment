package com.example.modul2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.modul2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var itemAdapter:Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createRecyclerView()
        setOnClickListener()
    }

    private fun createRecyclerView() {
        itemAdapter = Adapter(Data.getData(this))
        val recyclerView = binding.rvMain
        recyclerView.adapter = itemAdapter
    }

    private fun setOnClickListener() {
        itemAdapter.setOnClickListener(object :
        Adapter.OnClickListener{
            override fun onClick(position: Int, model: Manga) {
                val moveToDetail = Intent(this@MainActivity, DetailActivity::class.java)
                moveToDetail.putExtra(DetailActivity.MANGA, model)
                startActivity(moveToDetail)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.profile -> {
                val moveToProfile = Intent(this@MainActivity, ProfileActivity::class.java)
                startActivity(moveToProfile)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}