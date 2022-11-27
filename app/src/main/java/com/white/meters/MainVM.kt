package com.white.meters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.white.meters.data.base.InitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val initRepository: InitRepository
) : ViewModel() {

    fun init() {
        viewModelScope.launch {
            try {
                initRepository.init()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}