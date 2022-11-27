package com.white.meters.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.white.meters.data.base.Consts
import com.white.meters.data.base.DetectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DetectRepositoryImpl @Inject constructor() : DetectRepository {

    override suspend fun processImage(bitmap: Bitmap): Bitmap? = withContext(Dispatchers.IO) {
        val client = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .build()

        val MEDIA_TYPE_PNG = "text/plain".toMediaTypeOrNull()

        val blob = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /* Ignored for PNGs */, blob)
        val bitmapdata: ByteArray = blob.toByteArray()

        var file: File? = null
        //val fos: FileOutputStream? = null

        try {
            file = File.createTempFile("test", "png")
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                "image.jpg",
                RequestBody.create(MEDIA_TYPE_PNG, file ?: return@withContext null)
            ).build()

        try {
            val req = Request.Builder()
                .url("${Consts.BASE_URL}${Consts.DETECT_ENDPOINT}")
                .post(requestBody)
                .build()

            val response = client.newCall(req).execute()
            return@withContext if (response.isSuccessful) {
                BitmapFactory.decodeStream(response.body?.byteStream())
            } else null
        } catch (e: Exception) {
            return@withContext null
        }
    }
}