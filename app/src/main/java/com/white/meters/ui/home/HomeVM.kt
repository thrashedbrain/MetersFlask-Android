package com.white.meters.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.white.meters.data.base.MeterRepository
import com.white.meters.data.models.MeterData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val meterRepository: MeterRepository
) : ViewModel() {

    val meters = meterRepository.observeMeters()
        .filterNotNull()
        .asLiveData()

    fun saveToHistory() = viewModelScope.launch {
        meterRepository.saveHistory()
    }

    fun checkMetersData(meterData: MeterData): Boolean {
        return meterData.coldBathMeterData != null
                && meterData.hotBathMeterData != null
                && meterData.coldKitchenMeterData != null
                && meterData.hotKitchenMeterData != null
    }
}