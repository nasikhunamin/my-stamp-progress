package com.example.myprogressdci.seekbar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import com.example.myprogressdci.R

object CpnSeekbarDciThumbDrawable {
    fun getDrawable(
        context: Context,
        thumbType: CpnSeekbarDciThumbTypeDrawable,
        value: Int? = 0
    ): Drawable{
        return when(thumbType){
            CpnSeekbarDciThumbTypeDrawable.MINIMUM -> {
                val view = View.inflate(context, R.layout.sb_dci_thumb_minimum, null)
                val bitmap = generateBitmap(view)
                BitmapDrawable(context.resources, bitmap)
            }
            CpnSeekbarDciThumbTypeDrawable.END -> {
                val view = View.inflate(context, R.layout.sb_dci_thumb_end, null)
                val txtEnd = view.findViewById<TextView>(R.id.ssb_end)
                txtEnd.text = value.toString()
                val bitmap = generateBitmap(view)
                BitmapDrawable(context.resources, bitmap)
            }
            CpnSeekbarDciThumbTypeDrawable.PROGRESS -> {
                val view = View.inflate(context, R.layout.sb_dci_thumb_progress, null)
                val txtEnd = view.findViewById<TextView>(R.id.ssb_progress)
                txtEnd.text = value.toString()
                val bitmap = generateBitmap(view)
                BitmapDrawable(context.resources, bitmap)
            }
        }
    }

    private fun generateBitmap(view: View): Bitmap {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        val bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.draw(canvas)

        return bitmap
    }
}

enum class CpnSeekbarDciThumbTypeDrawable{
    MINIMUM,
    END,
    PROGRESS
}