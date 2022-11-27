package com.white.meters.base.view

import android.content.res.ColorStateList
import android.view.View
import com.white.meters.R

fun View.setSuccessState() {
    this.backgroundTintList = ColorStateList.valueOf(this.context.getColor(R.color.colorGreenSuccess))
}