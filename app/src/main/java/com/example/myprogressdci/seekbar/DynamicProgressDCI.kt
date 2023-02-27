package com.example.myprogressdci.seekbar

import android.graphics.Bitmap
import android.graphics.RectF

data class DynamicProgressDCI(
    val track: RectF = RectF(),
    val bitmap: Bitmap,
    val value: Int,
)