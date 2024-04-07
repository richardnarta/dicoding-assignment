package com.dicoding.asclepius.view

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var currentImageUri: Uri
    private var classificationResult = arrayListOf<String>()
    private lateinit var ivPreview: ImageView
    private lateinit var btnGallery: Button
    private lateinit var btnAnalyze: Button
    private lateinit var loadingIndicator: LinearProgressIndicator

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val inputUri = result.data?.data as Uri

            val outputUri = File(cacheDir, "${getTimeStamp()}.jpg").toUri()
            currentImageUri = outputUri

            val listOfUri = listOf(inputUri, outputUri)
            cropImage.launch(listOfUri)
        }
    }

    private val uCropContract = object: ActivityResultContract<List<Uri>, Uri>(){
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            val inputUri = input[0]
            val outputUri = input[1]

            val uCropTool = UCrop.of(inputUri, outputUri)
                .withAspectRatio(1f,1f)
                .withMaxResultSize(224, 224)

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
            showImage(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentImageUri = Uri.EMPTY

        binding.apply {
            btnGallery = galleryButton
            btnAnalyze = analyzeButton
            ivPreview = previewImageView
            loadingIndicator = progressIndicator
        }

        startGallery()

        btnAnalyze.setOnClickListener{
            if (currentImageUri != Uri.EMPTY){
                loadingIndicator.isVisible = true
                analyzeImage()
            }else{
                showToast(resources.getString(R.string.empty_image))
            }
        }
    }

    private fun startGallery() {
        btnGallery.setOnClickListener {
            val intent = Intent()
            intent.action = ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, resources.getString(R.string.choose_picture))
            launcherIntentGallery.launch(chooser)
        }
    }

    private fun showImage(currentImageUri:Uri) {
        Glide.with(this)
            .load(currentImageUri).centerInside()
            .into(ivPreview)
    }

    private fun analyzeImage() {
        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener{
                override fun onError(error: String) {
                    showToast(error)
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    runOnUiThread {
                        results?.let {
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()){
                                if (classificationResult.isNotEmpty()){
                                    classificationResult.clear()
                                }

                                it[0].categories.map {item ->
                                    classificationResult.add(currentImageUri.toString())
                                    classificationResult.add(item.label)

                                    val score = NumberFormat.getPercentInstance()
                                        .format(item.score)
                                        .trim()
                                    classificationResult.add(score)
                                    classificationResult.add(getTimeStamp())
                                }
                                moveToResult()
                            }
                        }
                    }
                }
            }
        )

        if (currentImageUri != Uri.EMPTY) {
            imageClassifierHelper.classifyStaticImage(currentImageUri)
        }
    }

    private fun moveToResult() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.RESULT, classificationResult)
        startActivity(intent)
    }

    private fun moveToHistory() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun getTimeStamp(): String {
        return DateFormat.getDateTimeInstance(DateFormat.DEFAULT,
            DateFormat.DEFAULT,
            Locale.getDefault()).format(Date())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.saved_list -> moveToHistory()
        }

        return super.onOptionsItemSelected(item)
    }
}