package com.example.myprogressdci.seekbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.myprogressdci.R

class CpnSeekbarDciArrowView : View {
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

    private var mWidth = 0
    private var mHeight = 0

    private val path = Path()
    private val paint = Paint()

    private fun init() {
        mWidth = CpnSeekbarDciSizeUtils.dp2px(context, 10f)
        mHeight = CpnSeekbarDciSizeUtils.dp2px(context, 5f)

        // draw arrow
        path.moveTo(0f, 0f)
        path.lineTo(mWidth.toFloat(),0f)
        path.lineTo(mWidth / 2f, mHeight.toFloat())
        path.close()

        paint.color = ResourcesCompat.getColor(resources, R.color.purple_700, null)
        paint.isAntiAlias = true
        paint.strokeWidth = 1f
        paint.setShadowLayer(
            0f,
            mWidth.toFloat(),
            mHeight.toFloat(),
            ResourcesCompat.getColor(resources, android.R.color.black, null)
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(path, paint)
    }

    fun setColor(color: Int){
        paint.color = color
        invalidate()
    }
}