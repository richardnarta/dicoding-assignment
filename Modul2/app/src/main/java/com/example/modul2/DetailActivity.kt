package com.example.modul2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.example.modul2.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    companion object{
        const val MANGA = "manga"
    }

    private var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar!!.title = "Manga Description"

        val manga = intent.getParcelableExtra<Manga>(MANGA)

        title = manga?.title

        binding.apply {
            Glide.with(this@DetailActivity)
                .load(manga?.image)
                .centerInside().into(ivManga)
            tvTitle.text = manga?.title
            tvChapter.text = manga?.chapter
            tvRating.text = manga?.rating
            tvSynopsis.text = manga?.synopsis
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.profile -> {
                val moveToProfile = Intent(this@DetailActivity, ProfileActivity::class.java)
                startActivity(moveToProfile)
            }

            R.id.share -> {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, title.toString())
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}