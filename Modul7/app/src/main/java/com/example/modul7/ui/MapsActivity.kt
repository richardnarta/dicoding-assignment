package com.example.modul7.ui

import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.ViewModelProvider
import com.example.modul7.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.modul7.databinding.ActivityMapsBinding
import com.example.modul7.utils.Event
import com.example.modul7.utils.StoryResult
import com.example.modul7.viewmodel.StoryViewModel
import com.example.modul7.viewmodel.StoryViewModelFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val boundsBuilder = LatLngBounds.Builder()
    private lateinit var binding: ActivityMapsBinding

    private lateinit var factory: StoryViewModelFactory
    private lateinit var viewModel: StoryViewModel

    private lateinit var progressBar: ContentLoadingProgressBar

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            } else {
                showToast(resources.getString(R.string.permission_denied))
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.title = resources.getString(R.string.title_activity_maps)
        progressBar = binding.progressBarMap

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle()

        factory = StoryViewModelFactory.getInstance(this@MapsActivity)
        viewModel = ViewModelProvider(this@MapsActivity, factory)[StoryViewModel::class.java]

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        showMarker()
    }

    private fun showMarker() {
        factory = StoryViewModelFactory.getInstance(this@MapsActivity)
        viewModel = ViewModelProvider(this@MapsActivity, factory)[StoryViewModel::class.java]

        viewModel.getStoryLocation().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is StoryResult.Loading -> progressBar.show()

                    is StoryResult.Success -> {
                        progressBar.hide()
                        result.data.forEach { location->
                            val latLong = LatLng(location.lat!!, location.lon!!)
                            mMap.addMarker(MarkerOptions().position(latLong).title(location.name))
                            boundsBuilder.include(latLong)
                        }

                        val bounds: LatLngBounds = boundsBuilder.build()
                        val padding = (resources.displayMetrics.widthPixels * 0.10).toInt()
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                bounds,
                                resources.displayMetrics.widthPixels,
                                resources.displayMetrics.heightPixels,
                                padding
                            )
                        )
                    }

                    is StoryResult.Error -> {
                        progressBar.hide()
                        viewModel.snackBarText.value = Event(resources.getString(R.string.error_toast, result.error))
                    }
                }
            }
        }
    }

    private fun setMapStyle() {
        try {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
        } catch (_: Resources.NotFoundException) { }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true

        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun showToast (text: String) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }
}