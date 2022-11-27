package com.white.meters.data.repository

import com.white.meters.data.base.Consts
import com.white.meters.data.base.InitRepository
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType

class InitRepositoryImpl() : InitRepository {

    override suspend fun init() {
        val client = OkHttpClient().newBuilder()
            .build()
        val mediaType = "text/plain".toMediaType()
        val body: RequestBody = RequestBody.create(mediaType, "")
        val request: Request = Request.Builder()
            .url("${Consts.BASE_URL}${Consts.INIT_ENDPOINT}")
            .method("POST", body)
            .build()
        val response: Response = client.newCall(request).execute()
    }
}