package com.example.modul3.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.widget.ContentLoadingProgressBar
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.modul3.R
import com.example.modul3.databinding.ActivityDetailBinding
import com.example.modul3.ui.adapter.FragmentPageAdapter
import com.example.modul3.viewmodel.DetailViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import de.hdodenhof.circleimageview.CircleImageView

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel by viewModels<DetailViewModel>()

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: FragmentPageAdapter
    private lateinit var progressBar: ContentLoadingProgressBar

    private lateinit var userImageView: CircleImageView
    private lateinit var userTvName: TextView
    private lateinit var userTvUserName: TextView
    private lateinit var userTvFollower: TextView
    private lateinit var userTvFollowing: TextView

    companion object{
        const val USER_NAME = "user_name"
    }

    private lateinit var userName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userName = intent.getStringExtra(USER_NAME)!!
        detailViewModel.setUserName(userName)

        loadDetail()
        createLayout()

        progressBar = binding.progressBar
        detailViewModel.isLoading.observe(this){
            if (it) progressBar.show() else progressBar.hide()
        }

        detailViewModel.snackBarText.observe(this) {
            it.getContentIfNotHandled()?.let { text ->
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadDetail() {
        detailViewModel.loadUserDetail()

        binding.apply {
            userImageView = circleImage
            userTvName = tvName
            userTvUserName = tvUsername
            userTvFollower = tvFollower
            userTvFollowing = tvFollowing
        }

        detailViewModel.user.observe(this){
            Glide.with(this)
                .load(it?.avatarUrl)
                .centerInside().into(userImageView)

            if (it?.name != null){
                userTvName.text = it.name
            }else{
                userTvName.text = resources.getString(R.string.no_name)
            }

            userTvUserName.text = it?.login
            userTvFollower.text = resources.getString(R.string.follower, it?.followers.toString())
            userTvFollowing.text = resources.getString(R.string.following, it?.following.toString())
        }
    }

    private fun createLayout(){
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager

        tabLayout.addTab(tabLayout.newTab().setText("Followers"))
        tabLayout.addTab(tabLayout.newTab().setText("Following"))

        adapter = FragmentPageAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null){
                    viewPager.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }

    fun getUsername(): String{
        return userName
    }
}