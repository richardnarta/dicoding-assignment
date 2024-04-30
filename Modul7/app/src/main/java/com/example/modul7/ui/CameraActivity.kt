package com.example.modul7.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.modul7.R
import com.example.modul7.databinding.ActivityCameraBinding
import com.example.modul7.ui.AddStoryActivity.Companion.IMAGE_RESULT
import com.example.modul7.utils.CameraHelper
import com.example.modul7.utils.CameraHelper.rotateBitmap
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    private val uCropContract = object: ActivityResultContract<List<Uri>, Uri>(){
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            val inputUri = input[0]
            val outputUri = input[1]

            val uCropTool = UCrop.of(inputUri, outputUri)
                .withAspectRatio(4f,3f)
                .withMaxResultSize(960, 540)

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
            val timeStamp = SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault()).format(Date())
            val outputDirectory = CameraHelper.getOutputDirectory(this@CameraActivity)
            val outputFile = File(outputDirectory, "$timeStamp.jpg")

            val move = Intent(this, AddStoryActivity::class.java)

            try {
                contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(outputFile).use { output ->
                        input.copyTo(output)
                    }
                }
                move.putExtra(IMAGE_RESULT, uri.toString())
                move.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(move)
                finish()
            } catch (e: IOException) {
                e.printStackTrace()
                showToast(resources.getString(R.string.error_toast, e.message))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        binding.btnCapture.setOnClickListener {
            takePhoto()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(exc: ImageCaptureException) {
                    showToast(exc.toString())
                }

                override fun onCaptureSuccess(image: ImageProxy) {
                    val buffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.capacity())
                    buffer.get(bytes)

                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                    val rotatedBitmap = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        rotateBitmap(bitmap, 90)
                    } else {
                        bitmap
                    }

                    val inputUri = CameraHelper.bitmapToUri(rotatedBitmap, this@CameraActivity)

                    val timeStamp = SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault()).format(Date())
                    val outputUri = File(cacheDir, "$timeStamp.jpg").toUri()

                    val listOfUri = listOf(inputUri, outputUri)
                    cropImage.launch(listOfUri)

                    image.close()
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY).build()

            val preview = Preview.Builder()
                .setResolutionSelector(resolutionSelector)
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setResolutionSelector(resolutionSelector)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                showToast(exc.toString())
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                showToast(resources.getString(R.string.permission_denied))
                finish()
            }
        }
    }

    private fun showToast (text: String) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 20
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}