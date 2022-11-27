package com.white.meters.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.white.meters.data.base.MeterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryVM @Inject constructor(
    private val meterRepository: MeterRepository
) : ViewModel() {

    val history = meterRepository.observeHistory()
        .asLiveData()
}