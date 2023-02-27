package com.example.myprogressdci.seekbar

import android.content.Context
import android.graphics.Color

class CpnSeekbarDciBuilder internal constructor(
    private val context: Context
) {
    // seekbar
    val end = 100f
    val start = 0f
    val progress = 0f
    val minimum = 0f
    var isClearPadding = false

    // track
    var trackSize = 0
    var trackBackgroundColor = Color.parseColor("#D7D7D7")
    var trackProgressColor = Color.parseColor("#000000")

    // thumb
    var thumbSize = 0

    init {
        trackSize = CpnSeekbarDciSizeUtils.dp2px(context, 8f)
        thumbSize = CpnSeekbarDciSizeUtils.dp2px(context, 25f)
    }
}