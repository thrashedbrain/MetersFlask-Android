package com.white.meters.data.base

import android.graphics.Bitmap

interface DetectRepository {

    suspend fun processImage(bitmap: Bitmap): Bitmap?
}