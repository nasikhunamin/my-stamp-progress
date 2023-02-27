package com.example.myprogressdci.seekbar

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import com.example.myprogressdci.R

class CpnSeekbarDciIndicator(
    private val indicatorView: View
) {
    val mIndicatorView: View get() = indicatorView

    private var arrowView: CpnSeekbarDciArrowView? = null
    private var txtLabel: TextView? = null

    init {
        arrowView = mIndicatorView.findViewById(R.id.ssb_indicator_arrow)
        txtLabel = mIndicatorView.findViewById(R.id.ssb_indicator_text)
    }

    fun updateIndicatorLocation(indicatorOffset: Int) {
        setMargin(mIndicatorView, indicatorOffset)
    }

    private fun setMargin(view: View?, left: Int) {
        if (view == null) return
        if (view.layoutParams is MarginLayoutParams){
            val layoutParams = view.layoutParams as MarginLayoutParams
            layoutParams.setMargins(
                if (left == -1) layoutParams.leftMargin else left,
                layoutParams.topMargin,
                layoutParams.rightMargin,
                layoutParams.bottomMargin,
            )
            view.requestLayout()
        }
    }

    fun updateArrowLocation(arrowOffset: Int) {
        setMargin(arrowView, arrowOffset)
    }

    fun updateLabelText(text: String) {
        txtLabel?.text = text
    }

}