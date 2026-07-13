package com.example.eatmate.data.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageCompressor @Inject constructor() {

    companion object {
        private const val MAX_EDGE = 1024
        private const val JPEG_QUALITY = 75
    }

    /**
     * Compress and encode image bytes for API submission.
     * Returns a base64 string ready for the Qwen API (no data: prefix).
     */
    fun compressAndEncode(imageBytes: ByteArray): String {
        // Decode
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)

        // Calculate scale
        val scale = maxOf(
            options.outWidth / MAX_EDGE,
            options.outHeight / MAX_EDGE,
            1
        )

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = scale
        }
        val bitmap = BitmapFactory.decodeByteArray(
            imageBytes, 0, imageBytes.size, decodeOptions
        ) ?: return ""

        // Compress to JPEG
        val output = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, output)
        val compressed = output.toByteArray()

        // Cleanup
        bitmap.recycle()

        return Base64.encodeToString(compressed, Base64.NO_WRAP)
    }
}
