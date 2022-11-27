package com.white.meters.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MeterData(
    var coldBathMeterData: ColdBathMeterData? = null,
    var hotBathMeterData: HotBathMeterData? = null,
    var coldKitchenMeterData: ColdKitchenMeterData? = null,
    var hotKitchenMeterData: HotKitchenMeterData? = null
) : Parcelable
