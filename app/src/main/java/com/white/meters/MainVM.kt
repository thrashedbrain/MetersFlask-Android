package com.white.meters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.white.meters.data.base.InitRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainVM @Inject constructor(
    private val initRepository: InitRepository
) : ViewModel() {

    fun init() {
        viewModelScope.launch {
            initRepository.init()
        }
    }
}