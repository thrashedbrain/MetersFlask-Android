package com.white.meters.data.models
import android.os.Parcelable
import com.white.meters.data.base.BaseMeterData
import kotlinx.parcelize.Parcelize

@Parcelize
data class HotBathMeterData(override val value: String) : Parcelable, BaseMeterData
