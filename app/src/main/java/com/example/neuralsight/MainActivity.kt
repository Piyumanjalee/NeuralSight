package com.example.neuralsight

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var viewFinder: PreviewView
    private lateinit var overlayView: OverlayView
    private lateinit var btnBack: ImageButton
    private lateinit var cameraExecutor: ExecutorService
    private var objectDetector: ObjectDetector? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, "කැමරාව සඳහා අවසර ලබා දිය යුතුය.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewFinder = findViewById(R.id.viewFinder)
        overlayView = findViewById(R.id.overlayView)
        btnBack = findViewById(R.id.btnBack)
        cameraExecutor = Executors.newSingleThreadExecutor()

        btnBack.setOnClickListener {
            finish()
        }

        setupObjectDetector()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun setupObjectDetector() {
        // AI මොඩල් එකේ සැකසුම් (50% ට වඩා විශ්වාස නම් පමණක් පෙන්නන්න)
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(5)
            .setScoreThreshold(0.5f)
            .build()

        try {
            // අපි ඩවුන්ලෝඩ් කරපු අලුත් මොඩල් එක ලෝඩ් කිරීම
            objectDetector = ObjectDetector.createFromFileAndOptions(
                this,
                "ssd_mobilenet.tflite", // ඔයාගේ tflite ෆයිල් එකේ නම
                options
            )
        } catch (e: Exception) {
            Toast.makeText(this, "Model එක ලෝඩ් කිරීම අසාර්ථකයි!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        analyzeImage(imageProxy)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis
                )
            } catch(exc: Exception) {
                Toast.makeText(this, "කැමරාව ආරම්භ කිරීම අසාර්ථකයි.", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    private fun analyzeImage(imageProxy: ImageProxy) {
        val bitmap = imageProxy.toBitmap()

        if (bitmap != null) {
            val image = TensorImage.fromBitmap(bitmap)

            // රූපය AI එකට යවා ප්‍රතිඵල ලබාගැනීම
            val results = objectDetector?.detect(image)

            runOnUiThread {
                if (results != null) {
                    // ප්‍රතිඵල ටික කොටු අඳින්න OverlayView එකට යැවීම
                    overlayView.setResults(results, bitmap.width, bitmap.height)
                }
            }
        }
        imageProxy.close()
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}