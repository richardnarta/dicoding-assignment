package com.example.modul7.ui

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.widget.ContentLoadingProgressBar
import com.bumptech.glide.Glide
import com.example.modul7.R
import com.example.modul7.databinding.ActivityAddStoryBinding
import com.example.modul7.utils.Event
import com.example.modul7.utils.ImageFileHelper.reduceFileImage
import com.example.modul7.utils.ImageFileHelper.uriToFile
import com.example.modul7.utils.StatusResult
import com.example.modul7.viewmodel.StoryViewModel
import com.example.modul7.viewmodel.StoryViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yalantis.ucrop.UCrop
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding

    private val factory: StoryViewModelFactory = StoryViewModelFactory.getInstance(this)
    private val viewModel: StoryViewModel by viewModels {
        factory
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    viewModel.snackBarText.value = Event(resources.getString(R.string.permission_denied))
                    showToast()
                    checkBoxLocation.isChecked = false
                }
            }
        }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var currentImageUri: Uri
    private lateinit var content: ContentResolver
    private val locationCoordinate = mutableListOf<Double?>(null, null)

    private lateinit var ivPhoto: ImageView
    private lateinit var galleryButton: Button
    private lateinit var cameraButton: Button
    private lateinit var inputDescription: EditText
    private lateinit var checkBoxLocation: CheckBox
    private lateinit var uploadButton: Button
    private lateinit var progressBar: ContentLoadingProgressBar

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val inputUri = result.data?.data as Uri

            val timeStamp = SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault()).format(Date())
            val outputUri = File(cacheDir, "${timeStamp}.jpg").toUri()

            val listOfUri = listOf(inputUri, outputUri)
            cropImage.launch(listOfUri)
        }
    }

    private val uCropContract = object: ActivityResultContract<List<Uri>, Uri>(){
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            val inputUri = input[0]
            val outputUri = input[1]

            val uCropTool = UCrop.of(inputUri, outputUri)
                .withAspectRatio(4f,3f)
                .withMaxResultSize(1920, 1080)

            return uCropTool.getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri {
            return if (resultCode == RESULT_OK && intent != null) {
                UCrop.getOutput(intent)!!
            } else {
                Uri.EMPTY
            }
        }
    }

    private val cropImage = registerForActivityResult(uCropContract){uri ->
        if (uri != Uri.EMPTY){
            currentImageUri = uri
            showImage(currentImageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.title = resources.getString(R.string.add_story_title)

        currentImageUri = Uri.EMPTY
        content = applicationContext.contentResolver

        binding.apply {
            ivPhoto = ivAddPhoto
            galleryButton = btnGallery
            cameraButton = btnCamera
            inputDescription = edAddDescription
            checkBoxLocation = cbLocation
            uploadButton = buttonAdd
            progressBar = progressBarAddStory
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        startGallery()
        startCamera()
        uploadStory()
        getMyLastLocation()
    }

    private fun uploadStory() {
        uploadButton.setOnClickListener {
            val description = inputDescription.text.toString()
            if (inputDescription.text.isEmpty()) {
                viewModel.snackBarText.value = Event(resources.getString(R.string.empty_desc))
                showToast()
            } else if (currentImageUri == Uri.EMPTY) {
                viewModel.snackBarText.value = Event(resources.getString(R.string.empty_image))
            } else {

                val imageFile = uriToFile(currentImageUri, this@AddStoryActivity).reduceFileImage()
                val requestBody = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )

                viewModel.uploadStory(multipartBody, requestBody, locationCoordinate[0], locationCoordinate[1]).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is StatusResult.Loading -> progressBar.show()

                            is StatusResult.Success -> {
                                progressBar.hide()
                                moveToActivity(MainActivity::class.java, true)
                                viewModel.snackBarText.value = Event(resources.getString(R.string.upload_success))
                                showToast()
                            }

                            is StatusResult.Error -> {
                                progressBar.hide()
                                viewModel.snackBarText.value = Event(resources.getString(R.string.error_toast, result.error))
                                showToast()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startCamera() {
        cameraButton.setOnClickListener {
            moveToActivity(CameraActivity::class.java)
        }
    }

    private fun showImage(imageUri: Uri) {
        ivPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(this)
            .load(imageUri)
            .into(ivPhoto)
    }

    private fun startGallery() {
        galleryButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, resources.getString(R.string.choose_picture))
            launcherIntentGallery.launch(chooser)
        }
    }

    override fun onResume() {
        super.onResume()

        val capturedImage = intent.getStringExtra(IMAGE_RESULT)

        if (capturedImage != null) {
            showImage(capturedImage.toUri())
            currentImageUri = capturedImage.toUri()

            intent.removeExtra(IMAGE_RESULT)
        }
    }

    private fun getMyLastLocation() {
        checkBoxLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                ) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            locationCoordinate[0] = location.latitude
                            locationCoordinate[1] = location.longitude
                        }
                    }
                } else {
                    requestPermissionLauncher.launch(
                        arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            } else {
                locationCoordinate[0] = null
                locationCoordinate[1] = null
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun moveToActivity(cls: Class<*>, clear: Boolean = false) {
        val move = Intent(this, cls)
        move.putExtra(UPLOAD_RESULT, true)
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

    companion object{
        const val IMAGE_RESULT = "result"
        const val UPLOAD_RESULT = "upload"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}