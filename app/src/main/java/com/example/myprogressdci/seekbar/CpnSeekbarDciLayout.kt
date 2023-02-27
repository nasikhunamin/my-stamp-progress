package com.example.myprogressdci.seekbar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

class CpnSeekbarDciLayout : LinearLayout {
    constructor(context: Context?) : super(context){
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes){
        init()
    }

    private fun init() {
        orientation = VERTICAL
    }

    override fun onFinishInflate() {
        val childCount = childCount
        for (i in childCount - 1 downTo 0){
            setLayoutIndicator(getChildAt(i), i)
        }
        super.onFinishInflate()
    }

    private fun setLayoutIndicator(child: View, index: Int) {
        if (child is CpnSeekbarDci){
            val seekbar: CpnSeekbarDci = child
            val indicatorView = seekbar.indicatorView

            seekbar.setIndicatorStayAlways(true)

            check(indicatorView !is CpnSeekbarDci) { "Indicator View can not be a content view for Indicator in case this inflating loop!" }

            val params = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )

            val layoutParams = MarginLayoutParams(params)
            layoutParams.setMargins(
                layoutParams.leftMargin,
                layoutParams.topMargin,
                layoutParams.rightMargin,
                CpnSeekbarDciSizeUtils.dp2px(context, 2f) - seekbar.paddingTop
            )

            addView(indicatorView, index, layoutParams)
            seekbar.showStayIndicator()
        }
    }
}