package com.white.meters.data.repository

import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.white.meters.data.base.MeterRepository
import com.white.meters.data.models.*
import com.white.meters.di.HistorySharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class MeterRepositoryImpl @Inject constructor(
    @HistorySharedPreferences private val historySharedPreferences: SharedPreferences,
) : MeterRepository {

    private var meterData = MeterData()
    private val meterDataFlow = MutableStateFlow(meterData)

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val listData = Types.newParameterizedType(List::class.java, HistoryData::class.java)
    private val historyMeterDataJsonAdapter: JsonAdapter<List<HistoryData>> = moshi.adapter(listData)

    private val historyMeterDataFlow = MutableStateFlow(getHistoryCache())

    private fun check() {
    }

    override fun setHotKitchen(hotKitchenMeterData: HotKitchenMeterData) {
        meterData.hotKitchenMeterData = hotKitchenMeterData
        check()
    }

    override fun setColdKitchen(coldKitchenMeterData: ColdKitchenMeterData) {
        meterData.coldKitchenMeterData = coldKitchenMeterData
        check()
    }

    override fun setHotBath(hotBathMeterData: HotBathMeterData) {
        meterData.hotBathMeterData = hotBathMeterData
        check()
    }

    override fun setColdBath(coldBathMeterData: ColdBathMeterData) {
        meterData.coldBathMeterData = coldBathMeterData
        check()
    }

    override fun observeMeters(): Flow<MeterData> = meterDataFlow

    override fun observeHistory(): Flow<List<HistoryData>?> = historyMeterDataFlow

    private fun getHistoryCache(): List<HistoryData>? {
        return try {
            val json = historySharedPreferences.getString(KEY_METERS, null)
            json?.let { historyMeterDataJsonAdapter.fromJson(json) }
        } catch (exception: Exception) {
            exception.printStackTrace()
            null
        }
    }

    override suspend fun saveHistory() {
        withContext(Dispatchers.IO) {
            val tempHistory = historyMeterDataFlow.value
            val newList = mutableListOf<HistoryData>()
            if (!tempHistory.isNullOrEmpty()) {
                newList.addAll(tempHistory)
            }
            newList.add(HistoryData(meterData, Date().time))
            historyMeterDataFlow.value = newList
            val json = historyMeterDataJsonAdapter.toJson(newList)
            historySharedPreferences.edit()
                .putString(KEY_METERS, json)
                .apply()
        }
    }

    companion object {
        val KEY_METERS = "meters_key"
    }
}