package com.white.meters.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetectArgs (val meterType: MeterType) : Parcelable