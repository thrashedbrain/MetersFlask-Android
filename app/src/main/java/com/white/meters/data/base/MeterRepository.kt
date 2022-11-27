package com.white.meters.data.base

import com.white.meters.data.models.*
import kotlinx.coroutines.flow.Flow

interface MeterRepository {

    fun setHotKitchen(hotKitchenMeterData: HotKitchenMeterData)
    fun setColdKitchen(coldKitchenMeterData: ColdKitchenMeterData)
    fun setHotBath(hotBathMeterData: HotBathMeterData)
    fun setColdBath(coldBathMeterData: ColdBathMeterData)

    fun observeMeters(): Flow<MeterData>

    fun observeHistory(): Flow<List<HistoryData>?>

    suspend fun saveHistory()

}