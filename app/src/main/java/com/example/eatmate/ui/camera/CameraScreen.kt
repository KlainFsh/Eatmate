package com.example.eatmate.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.activity.compose.LocalActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.eatmate.ui.components.ShutterButton
import com.example.eatmate.ui.theme.BrandOrange
import com.example.eatmate.ui.theme.BrandPeach
import com.example.eatmate.ui.theme.BrandWarm
import com.example.eatmate.ui.theme.SurfaceLight
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CameraScreen(
    onNavigateToResult: (String) -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    viewModel: CameraViewModel = hiltViewModel(LocalActivity.current as androidx.activity.ComponentActivity)
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var hasPermission by remember { mutableStateOf(false) }
    var cameraError by remember { mutableStateOf<String?>(null) }
    var capturedImagePath by remember { mutableStateOf<String?>(null) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        cameraError = null
        if (granted) {
            startCamera(
                context, lifecycleOwner, cameraExecutor,
                previewView,
                onImageCaptureReady = { imageCapture = it },
                onError = { cameraError = it }
            )
        }
    }

    // Gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.resetState()
            try {
                val bytes = readGalleryImageBytes(context, uri)
                if (bytes != null && bytes.isNotEmpty()) {
                    val file = File(
                        context.cacheDir,
                        "eatmate_gallery_${
                            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                        }.jpg"
                    )
                    file.writeBytes(bytes)
                    capturedImagePath = file.absolutePath
                    viewModel.analyzePhoto(bytes)
                } else {
                    viewModel.setError("无法读取图片，请重试")
                }
            } catch (e: OutOfMemoryError) {
                viewModel.setError("图片过大，请选择较小的图片")
            } catch (e: Exception) {
                Log.e("Eatmate", "Gallery error", e)
                viewModel.setError("读取相册图片失败")
            }
        }
    }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        hasPermission = granted
        if (!granted) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Navigate to result when analysis completes
    LaunchedEffect(uiState.analysisResult) {
        val path = capturedImagePath
        if (uiState.analysisResult != null && path != null) {
            onNavigateToResult(path)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera preview or permission/error placeholder
        if (hasPermission && cameraError == null) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).also { pv ->
                        previewView = pv
                        startCamera(
                            ctx, lifecycleOwner, cameraExecutor, pv,
                            onImageCaptureReady = { imageCapture = it },
                            onError = { cameraError = it }
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else if (cameraError != null) {
            // Camera failed to start — show error with retry
            Box(
                modifier = Modifier.fillMaxSize().background(BrandWarm),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)) {
                    Text("📷", style = MaterialTheme.typography.displayMedium)
                    Spacer(Modifier.height(16.dp))
                    Text(cameraError!!, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(24.dp))
                    Surface(
                        onClick = {
                            cameraError = null
                            previewView?.let { pv ->
                                startCamera(context, lifecycleOwner, cameraExecutor, pv,
                                    onImageCaptureReady = { imageCapture = it },
                                    onError = { cameraError = it })
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = BrandOrange
                    ) {
                        Text("重试", modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF3D2E1F))
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize().background(BrandWarm),
                contentAlignment = Alignment.Center
            ) {
                Text("需要相机权限才能拍照",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface)
            }
        }

        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding()
                .background(Color(0xFF3D2E1F).copy(alpha = 0.4f))
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回", tint = Color.White)
                }
            } else {
                Spacer(Modifier.width(8.dp))
            }
            Text("智能识菜", style = MaterialTheme.typography.titleLarge,
                color = Color.White, modifier = Modifier.weight(1f))
        }

        // Loading overlay
        if (uiState.isAnalyzing) {
            LoadingOverlay()
        }

        // Error banner
        uiState.error?.let { error ->
            Box(
                modifier = Modifier.align(Alignment.Center).padding(32.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(error,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        }

        // Bottom area
        Box(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .background(BrandPeach.copy(alpha = 0.5f))
                .padding(vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                GalleryButton(
                    enabled = !uiState.isAnalyzing,
                    onClick = { galleryLauncher.launch("image/*") }
                )
                Spacer(Modifier.weight(1f))
                ShutterButton(
                    enabled = hasPermission && !uiState.isAnalyzing,
                    onClick = {
                        val file = File(context.cacheDir,
                            "eatmate_${
                                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                            }.jpg")
                        val path = file.absolutePath
                        capturedImagePath = path
                        viewModel.resetState()

                        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                        imageCapture?.takePicture(
                            outputOptions, cameraExecutor,
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                    viewModel.analyzePhoto(file.readBytes())
                                }
                                override fun onError(ex: ImageCaptureException) {
                                    viewModel.setError("拍照失败: ${ex.message}")
                                }
                            }
                        )
                    }
                )
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.size(44.dp))
            }
        }
    }
}

private fun startCamera(
    context: android.content.Context,
    lifecycleOwner: LifecycleOwner,
    executor: ExecutorService,
    previewView: PreviewView?,
    onImageCaptureReady: (ImageCapture) -> Unit,
    onError: (String) -> Unit = {}
) {
    if (previewView == null) return

    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        try {
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
            onImageCaptureReady(imageCapture)
        } catch (e: Exception) {
            Log.e("Eatmate", "Camera init failed", e)
            onError("相机启动失败: ${e.message}\n请确认已授予相机权限后重试")
        }
    }, ContextCompat.getMainExecutor(context))
}

@Composable
private fun GalleryButton(enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(44.dp).clip(CircleShape)
            .background(SurfaceLight.copy(alpha = if (enabled) 0.9f else 0.4f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Filled.PhotoLibrary, "从相册选择",
            tint = if (enabled) Color(0xFF3D2E1F) else Color(0xFF3D2E1F).copy(alpha = 0.3f),
            modifier = Modifier.size(22.dp))
    }
}

private fun readGalleryImageBytes(context: android.content.Context, uri: Uri): ByteArray? {
    return context.contentResolver.openInputStream(uri)?.use { stream ->
        val bytes = stream.readBytes()
        if (bytes.size > 4 * 1024 * 1024) {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
            val scale = maxOf(options.outWidth / 1024, options.outHeight / 1024, 1)
            val decodeOpts = BitmapFactory.Options().apply { inSampleSize = scale }
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, decodeOpts)
            val output = java.io.ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, output)
            bitmap?.recycle()
            output.toByteArray()
        } else bytes
    }
}

@Composable
private fun LoadingOverlay() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface) {
            Row(Modifier.padding(horizontal = 28.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(color = BrandOrange,
                    modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
                Spacer(Modifier.width(14.dp))
                Text("AI 正在分析中…", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
