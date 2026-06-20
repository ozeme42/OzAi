package com.example.ui.features

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.BuildConfig
import com.example.api.*
import kotlinx.coroutines.launch
import android.util.Base64
import java.io.ByteArrayInputStream
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageStudioScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var prompt by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    
    // Ratios
    var selectedAspectRatio by remember { mutableStateOf("1:1") }
    val aspectRatios = listOf("1:1", "3:4", "4:3", "16:9")

    var inputImageUri by remember { mutableStateOf<Uri?>(null) }
    var inputImageBase64 by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        inputImageUri = uri
        uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                }
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                inputImageBase64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
            } catch (e: Exception) {
                errorMessage = "Görsel yüklenirken hata: ${e.localizedMessage}"
            }
        }
    }

    fun generateImage() {
        if (prompt.isBlank()) return
        keyboardController?.hide()
        isGenerating = true
        errorMessage = null

        coroutineScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isBlank()) {
                    errorMessage = "Gemini API anahtarı bulunamadı (.env dosyasına ekleyin)."
                    isGenerating = false
                    return@launch
                }

                val parts = mutableListOf(Part(text = prompt))
                if (inputImageBase64 != null) {
                    parts.add(Part(inlineData = InlineData(mimeType = "image/jpeg", data = inputImageBase64!!)))
                }

                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(parts = parts)
                    ),
                    generationConfig = GenerationConfig(
                        imageConfig = ImageConfig(
                            aspectRatio = selectedAspectRatio,
                            imageSize = "1K",
                            numberOfImages = 1,
                            outputMimeType = "image/jpeg"
                        ),
                        responseModalities = listOf("IMAGE")
                    )
                )

                val response = RetrofitClient.service.generateContent("gemini-3.1-flash-image-preview", apiKey, request)
                val base64Data = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.inlineData?.data
                
                if (base64Data != null) {
                    val bytes = Base64.decode(base64Data, Base64.DEFAULT)
                    generatedImageBytes = bytes
                } else {
                    errorMessage = "Görsel üretilemedi. Model başka bir formatta yanıt vermiş olabilir."
                }
            } catch (e: Exception) {
                errorMessage = "Görsel üretilirken bir hata oluştu: ${e.localizedMessage}"
            } finally {
                isGenerating = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yapay Zeka Görsel Stüdyosu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            
            Card(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (isGenerating) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Görseliniz işleniyor...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else if (generatedImageBytes != null) {
                    val context = LocalContext.current
                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(generatedImageBytes)
                            .build()
                    )
                    Image(
                        painter = painter,
                        contentDescription = "Üretilen Görsel",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Görseliniz burada görünecek", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Text("Ne çizmek istiyorsunuz?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("Örnek: Teleskopla yıldızlara bakan küçük bir sincap illüstrasyonu") },
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = { generateImage() }),
                enabled = !isGenerating,
                trailingIcon = {
                    IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Icon(Icons.Default.AttachFile, contentDescription = "Referans Görsel Ekle")
                    }
                }
            )

            if (inputImageUri != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val painter = rememberAsyncImagePainter(model = inputImageUri)
                    Image(
                        painter = painter,
                        contentDescription = "Referans Görsel",
                        modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Referans eklendi", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { 
                        inputImageUri = null
                        inputImageBase64 = null
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Referans Görseli Kaldır", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text("En Boy Oranı", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                aspectRatios.forEach { ratio ->
                    FilterChip(
                        selected = selectedAspectRatio == ratio,
                        onClick = { selectedAspectRatio = ratio },
                        label = { Text(ratio) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { generateImage() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !isGenerating && prompt.isNotBlank()
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Görsel Üret", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
