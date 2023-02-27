package com.example.myprogressdci.seekbar

import android.content.Context
import android.util.TypedValue

object CpnSeekbarDciSizeUtils {
    fun dp2px(context: Context, dpValue: Float): Int{
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue,
            context.resources.displayMetrics
        ).toInt()

    }
}