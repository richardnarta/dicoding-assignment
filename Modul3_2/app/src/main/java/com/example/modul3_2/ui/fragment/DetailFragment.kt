package com.example.modul3_2.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.modul3_2.R
import com.example.modul3_2.data.local.entity.UserEntity
import com.example.modul3_2.databinding.FragmentDetailBinding
import com.example.modul3_2.ui.viewmodel.DetailViewModel
import com.example.modul3_2.ui.FragmentPageAdapter
import com.example.modul3_2.ui.MainActivity
import com.example.modul3_2.utils.Constants.SHARE_URL
import com.example.modul3_2.utils.ViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import de.hdodenhof.circleimageview.CircleImageView

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DetailViewModel

    private val args: DetailFragmentArgs by navArgs()

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: FragmentPageAdapter
    private lateinit var loadingProgressBar: ContentLoadingProgressBar

    private lateinit var userImageView: CircleImageView
    private lateinit var userTvName: TextView
    private lateinit var userTvUserName: TextView
    private lateinit var userTvFollower: TextView
    private lateinit var userTvFollowing: TextView

    private lateinit var fabFavorite: FloatingActionButton
    private lateinit var fabShareButton: FloatingActionButton
    private lateinit var userEntity: UserEntity
    private var isAdded: Boolean = false

    private var isLoaded = false

    companion object{
        lateinit var USER_NAME: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val actionBar = (activity as MainActivity).supportActionBar
        actionBar!!.title = resources.getString(R.string.detail_fragment)

        val factory = ViewModelFactory.getInstance(this.requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        USER_NAME = args.id

        if (savedInstanceState == null && !isLoaded){
            viewModel.setUserName(args.id)
            viewModel.loadUserDetail()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(layoutInflater, container, false)

        bindingItem()
        createLayout()
        fabSetOnClickListener()

        viewModel.snackBarText.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let{text ->
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun bindingItem(){
        binding.apply {
            userImageView = circleImage
            userTvName = tvName
            userTvUserName = tvUsername
            userTvFollower = tvFollower
            userTvFollowing = tvFollowing
            loadingProgressBar = progressBar
            fabFavorite = fabFav
            fabShareButton = fabShare
        }

        viewModel.apply {
            isLoading.observe(viewLifecycleOwner){ state ->
                if (state) loadingProgressBar.show() else loadingProgressBar.hide()
            }

            user.observe(viewLifecycleOwner){user ->
                userEntity = UserEntity(name = user?.name,
                    avatarUrl = user?.avatarUrl,
                    userName = user?.login,
                    followerCount = user?.followers,
                    followingCount = user?.following
                )

                Glide.with(requireActivity())
                    .load(user?.avatarUrl)
                    .centerInside().into(userImageView)

                if (user?.name != null){
                    userTvName.text = user.name
                }else{
                    userTvName.text = resources.getString(R.string.no_name)
                }

                userTvUserName.text = user?.login
                userTvFollower.text = resources.getString(R.string.follower, user?.followers.toString())
                userTvFollowing.text = resources.getString(R.string.following, user?.following.toString())
            }

            checkUser(USER_NAME){ isBookmarked ->
                if (isBookmarked == 1){
                    isAdded = true
                    fabFavorite.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_fav))
                }else{
                    isAdded = false
                    fabFavorite.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_unfav))
                }
            }
        }
    }

    private fun createLayout() {
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager

        tabLayout.addTab(tabLayout.newTab().setText(resources.getString(R.string.tab_followers)))
        tabLayout.addTab(tabLayout.newTab().setText(resources.getString(R.string.tab_following)))

        adapter = FragmentPageAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null){
                    viewPager.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }

    private fun fabSetOnClickListener(){
        fabFavorite.setOnClickListener {
            viewModel.apply {
                isAdded = if (isAdded){
                    delete(USER_NAME)
                    Snackbar.make(binding.root, resources.getString(R.string.success_remove_user), Snackbar.LENGTH_SHORT).show()
                    fabFavorite.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_unfav))
                    false
                }else{
                    insert(userEntity)
                    Snackbar.make(binding.root, resources.getString(R.string.success_add_user), Snackbar.LENGTH_SHORT).show()
                    fabFavorite.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_fav))
                    true
                }
            }
        }

        fabShareButton.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, SHARE_URL + USER_NAME)
                type = "text/html"
            }

            val shareIntent = Intent.createChooser(sendIntent, "Share $USER_NAME user profile")
            startActivity(shareIntent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean("loaded", isLoaded)
    }
}