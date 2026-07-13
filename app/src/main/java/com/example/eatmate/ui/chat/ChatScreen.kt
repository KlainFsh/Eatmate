package com.example.eatmate.ui.chat

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.eatmate.data.local.entity.ChatMessageEntity
import com.example.eatmate.ui.theme.BrandOrange
import com.example.eatmate.ui.theme.BrandPeach
import com.example.eatmate.ui.theme.BrandWarm
import com.example.eatmate.ui.theme.SurfaceLight
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    var textInput by remember { mutableStateOf("") }

    // Gallery launcher for image pick
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            Log.d("Eatmate.Chat", "Gallery: got uri=$uri")
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream == null) {
                    viewModel.setError("无法读取图片，请重试")
                    return@rememberLauncherForActivityResult
                }
                val bytes = inputStream.use { it.readBytes() }
                if (bytes.isEmpty()) {
                    viewModel.setError("图片数据为空，请重试")
                    return@rememberLauncherForActivityResult
                }
                Log.d("Eatmate.Chat", "Gallery: read ${bytes.size} bytes")

                // Copy to cache for display
                val cacheFile = java.io.File(context.cacheDir, "chat_img_${System.currentTimeMillis()}.jpg")
                cacheFile.writeBytes(bytes)
                Log.d("Eatmate.Chat", "Saved to cache: ${cacheFile.absolutePath}")

                viewModel.sendImage(bytes, cacheFile.absolutePath)
            } catch (e: Exception) {
                Log.e("Eatmate.Chat", "Gallery error", e)
                viewModel.setError("读取失败: ${e.message}")
            }
        } else {
            Log.e("Eatmate.Chat", "Gallery: uri is NULL!")
        }
    }

    // Auto-scroll to bottom on new messages
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(BrandOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💬", fontSize = 14.sp)
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(uiState.enenName, style = MaterialTheme.typography.titleMedium)
                            AnimatedVisibility(
                                visible = uiState.isEnenTyping,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Text(
                                    "对方正在输入...",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrandOrange
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                },
                windowInsets = TopAppBarDefaults.windowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
            )
        },
        bottomBar = {
            // Input bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                        Icon(Icons.Filled.CameraAlt, "拍照", tint = BrandOrange)
                    }
                    TextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("跟${uiState.enenName}聊聊...", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SurfaceLight,
                            unfocusedContainerColor = SurfaceLight,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = false,
                        maxLines = 3
                    )
                    Spacer(Modifier.width(4.dp))
                    IconButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                viewModel.sendText(textInput)
                                textInput = ""
                            }
                        },
                        enabled = textInput.isNotBlank()
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send, "发送",
                            tint = if (textInput.isNotBlank()) BrandOrange else Color.Gray
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Error banner
            if (uiState.error != null) {
                Surface(
                    color = Color(0xFFFFEBEE),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        uiState.error!!,
                        color = Color(0xFFC62828),
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (uiState.messages.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(80.dp).clip(CircleShape).background(BrandPeach),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💬", fontSize = 36.sp)
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "嗨，我是${uiState.enenName}～",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "拍张照或者告诉我你吃了什么，\n我来帮你看看营养怎么样 😋",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
                return@Box
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.messages, key = { it.id }) { msg ->
                    ChatBubble(msg, uiState.enenName)
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(msg: ChatMessageEntity, enenName: String) {
    val isUser = msg.role == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // Enen avatar
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(BrandOrange),
                contentAlignment = Alignment.Center
            ) {
                Text("🍽", fontSize = 14.sp, color = Color.White)
            }
            Spacer(Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 260.dp)
        ) {
            if (!isUser) {
                Text(
                    enenName,
                    style = MaterialTheme.typography.labelSmall,
                    color = BrandOrange,
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                )
            }

            // Food photo — show if imagePath or foodDataJson present
            if (isUser && (msg.imagePath != null || msg.foodDataJson != null)) {
                if (msg.imagePath != null && java.io.File(msg.imagePath).exists()) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(140.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(msg.imagePath)
                                .build(),
                            contentDescription = "食物照片",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else if (msg.foodDataJson != null) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandPeach.copy(alpha = 0.5f)),
                        modifier = Modifier.size(100.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text("📷 食物", fontSize = 14.sp, color = Color(0xFF5C4322))
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            // Text bubble — skip placeholder for image messages
            if (msg.content.isNotBlank() && msg.content != "[图片]") {
                val bubbleShape = if (isUser) {
                    RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp)
                } else {
                    RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp)
                }
                Surface(
                    color = if (isUser) BrandOrange else Color.White,
                    shape = bubbleShape,
                    shadowElevation = if (isUser) 0.dp else 1.dp
                ) {
                    Text(
                        msg.content,
                        modifier = Modifier.padding(12.dp),
                        color = if (isUser) Color.White else Color(0xFF333333),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (isUser) {
            Spacer(Modifier.width(8.dp))
            // User avatar
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(SurfaceLight),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 14.sp)
            }
        }
    }
}
