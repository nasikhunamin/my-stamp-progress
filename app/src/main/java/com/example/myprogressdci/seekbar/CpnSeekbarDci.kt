package com.example.myprogressdci.seekbar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import com.example.myprogressdci.R
import kotlin.math.roundToInt

class CpnSeekbarDci : View {
    private var paint = Paint()

    // seekbar
    private var paddingLeftSeekbar = 0
    private var paddingRightSeekbar = 0
    private var paddingTopSeekbar = 0
    private var measuredWidthSeekbar = 0

    private var lengthSeekbar = 0f

    private var startValue = 0f
    private var endValue = 0f
    private var minimumValue = 0f
    private var progressValue = 0f

    private var isClearPadding = false

    // indicator
    private var indicator: CpnSeekbarDciIndicator? = null
    private var indicatorStayAlways = true
    internal lateinit var indicatorView: View
        private set

    // track
    private var progressTrack = RectF()
    private var minimumTrack = RectF()
    private var backgroundTrack = RectF()

    private var trackSize = 0

    private var backgroundTrackColor = 0
    private var progressTrackColor = 0

    // thumb
    private var thumbRadius = 0f
    private var thumbSize = 0

    private var progressThumbDrawable: Drawable? = null
    private var minimumThumbDrawable: Drawable? = null
    private var endThumbDrawable: Drawable? = null

    private val normalPadding = CpnSeekbarDciSizeUtils.dp2px(context, 16f)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(context, attrs)
        initSeekbar()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttrs(context, attrs)
        initSeekbar()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initAttrs(context, attrs)
        initSeekbar()
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val builder = CpnSeekbarDciBuilder(context)
        if (attrs == null) {
            this.apply(builder)
            return
        }

        val ta = context.obtainStyledAttributes(attrs, R.styleable.CpnSeekbarDci)

        // seekbar
        endValue = ta.getFloat(R.styleable.CpnSeekbarDci_sb_dci_end, builder.end)
        startValue = ta.getFloat(R.styleable.CpnSeekbarDci_sb_dci_start, builder.start)
        progressValue = ta.getFloat(R.styleable.CpnSeekbarDci_sb_dci_progress, builder.progress)
        minimumValue = ta.getFloat(R.styleable.CpnSeekbarDci_sb_dci_min, builder.minimum)
        isClearPadding = ta.getBoolean(R.styleable.CpnSeekbarDci_sb_dci_is_clear_padding, builder.isClearPadding)

        // track
        trackSize = ta.getDimensionPixelSize(R.styleable.CpnSeekbarDci_sb_dci_track_size, builder.trackSize)
        backgroundTrackColor = ta.getColor(R.styleable.CpnSeekbarDci_sb_dci_track_background_color, builder.trackBackgroundColor)
        progressTrackColor = ta.getColor(R.styleable.CpnSeekbarDci_sb_dci_track_progress_color, builder.trackProgressColor)

        // thumb
        thumbSize = ta.getDimensionPixelSize(R.styleable.CpnSeekbarDci_sb_dci_thumb_size, builder.thumbSize)

        indicatorView = inflate(context, R.layout.sb_dci_indicator_view, null)
        minimumThumbDrawable = CpnSeekbarDciThumbDrawable.getDrawable(context, CpnSeekbarDciThumbTypeDrawable.MINIMUM)

        ta.recycle()
    }

    private fun initSeekbar() {
        initRangeValue()

        thumbRadius = CpnSeekbarDciSizeUtils.dp2px(context, THUMB_MAX_WIDTH).coerceAtLeast(thumbSize) / 2.0f
        paint.strokeCap = Paint.Cap.ROUND
        paint.isAntiAlias = true

        initDefaultPadding()
        initIndicatorView()
    }

    private fun apply(builder: CpnSeekbarDciBuilder) {
        // seekbar
        endValue = builder.end
        startValue = builder.start
        progressValue = builder.progress
        minimumValue = builder.minimum
        isClearPadding = builder.isClearPadding

        // track
        trackSize = builder.trackSize
        backgroundTrackColor = builder.trackBackgroundColor
        progressTrackColor = builder.trackProgressColor

        // thumb
        thumbSize = builder.thumbSize
    }

    private fun initRangeValue() {
        require(endValue >= startValue) { "The argument end value must be larger than start value" }
        if (progressValue < startValue) progressValue = startValue
        if (progressValue > endValue) progressValue = endValue
    }

    private fun initDefaultPadding() {
        if (!isClearPadding) {
            if (paddingLeft == 0) setPadding(normalPadding, paddingTop, paddingRight, paddingBottom)
            if (paddingRight == 0) setPadding(paddingLeft, paddingTop, normalPadding, paddingBottom)
        }
    }

    private fun initIndicatorView() {
        if (indicator == null) {
            indicator = CpnSeekbarDciIndicator(indicatorView)
            indicatorView = indicator?.mIndicatorView ?: return
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = (thumbRadius * 2 + paddingTop + paddingBottom).roundToInt()
        setMeasuredDimension(
            resolveSize(
                CpnSeekbarDciSizeUtils.dp2px(context, 170f),
                widthMeasureSpec
            ), height
        )

        initSeekbarInfo()
        refreshSeekbarLocation()
    }


    private fun initSeekbarInfo() {
        measuredWidthSeekbar = measuredWidth
        paddingLeftSeekbar = paddingStart
        paddingRightSeekbar = paddingEnd
        paddingTopSeekbar = paddingTop
        lengthSeekbar = (measuredWidthSeekbar - paddingLeftSeekbar - paddingRightSeekbar).toFloat()
    }

    private fun refreshSeekbarLocation() {
        initTrackLocation()
        refreshThumbPosition(progressValue)
    }


    private fun initTrackLocation() {
        progressTrack.left = paddingLeftSeekbar.toFloat()
        progressTrack.top = paddingTopSeekbar + thumbRadius
        progressTrack.right = (progressValue - startValue) * lengthSeekbar / amplitude + paddingLeftSeekbar
        progressTrack.bottom = progressTrack.top

        backgroundTrack.left = progressTrack.right
        backgroundTrack.top = progressTrack.bottom
        backgroundTrack.right = (measuredWidthSeekbar - paddingRightSeekbar).toFloat()
        backgroundTrack.bottom = progressTrack.bottom

        minimumTrack.left = paddingLeftSeekbar.toFloat()
        minimumTrack.top = paddingTopSeekbar + thumbRadius
        minimumTrack.right = (minimumValue - startValue) * lengthSeekbar / amplitude + paddingLeftSeekbar
        minimumTrack.bottom = progressTrack.top
    }

    private val amplitude: Float
        get() = if (endValue - startValue > 0) endValue - startValue else 1f

    private fun refreshThumbPosition(progressValue: Float) {
        progressTrack.right = (progressValue - startValue) * lengthSeekbar / amplitude + paddingLeftSeekbar
        backgroundTrack.left = progressTrack.right
        minimumTrack.left = (minimumValue - startValue) * lengthSeekbar / amplitude + paddingLeftSeekbar
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
         /*drawTrack(canvas)
         drawThumb(canvas)*/
        drawMultipleTrack(canvas)
    }

    private fun drawMultipleTrack(canvas: Canvas) {
        /*
        * 1 Progress
        * 2 Minimum
        * 3 End
        * */

        // draw end track
        paint.color = backgroundTrackColor
        paint.strokeWidth = trackSize.toFloat()
        canvas.drawLine(
            backgroundTrack.left,
            backgroundTrack.top,
            backgroundTrack.right,
            backgroundTrack.bottom,
            paint
        )

        val dynamicProgress = arrayListOf<DynamicProgressDCI>()
        list.forEach { item ->
            val progressThumbDrawable = CpnSeekbarDciThumbDrawable.getDrawable(
                context,
                if (item < progressValue) CpnSeekbarDciThumbTypeDrawable.PROGRESS else CpnSeekbarDciThumbTypeDrawable.END,
                item
            )

            val paint = Paint()
            val track = RectF()

            track.left = paddingLeftSeekbar.toFloat()
            track.top = paddingTopSeekbar + thumbRadius
            track.right = (item - startValue) * lengthSeekbar / amplitude + paddingLeftSeekbar
            track.bottom = track.top

            paint.color = ResourcesCompat.getColor(resources, android.R.color.darker_gray, null)
            paint.strokeWidth = trackSize.toFloat()

            val progressBitmap = getDrawBitmap(progressThumbDrawable) ?: throw IllegalArgumentException("Progress thumb drawable not found!")
            dynamicProgress.add(DynamicProgressDCI(track, progressBitmap, item))
        }

        val isProgressNow = dynamicProgress.firstOrNull { it.value == progressValue.roundToInt() }
        val dynamicHigherP = dynamicProgress.reversed().filter { it.value > progressValue }
        dynamicHigherP.forEach {
            val track = it.track
            val progressBitmap = it.bitmap

            canvas.drawLine(
                track.left,
                track.top,
                track.right,
                track.bottom,
                paint
            )

            val progressCenterX = track.right
            canvas.drawBitmap(
                progressBitmap,
                progressCenterX - progressBitmap.width / 2.0f,
                track.top - progressBitmap.height / 2.0f,
                paint
            )
        }

        // draw progress track
        paint.color = progressTrackColor
        paint.strokeWidth = trackSize.toFloat()
        canvas.drawLine(
            progressTrack.left,
            progressTrack.top,
            progressTrack.right,
            progressTrack.bottom,
            paint
        )

        if (isProgressNow != null){
            drawMultipleThumb(canvas)
        }

        val dynamicLowerP = dynamicProgress.reversed().filter { it.value < progressValue }
        if (dynamicLowerP.isNotEmpty()){
            dynamicLowerP.forEach {
                val track = it.track
                val progressBitmap = it.bitmap

                canvas.drawLine(
                    track.left,
                    track.top,
                    track.right,
                    track.bottom,
                    paint
                )

                val progressCenterX = track.right
                canvas.drawBitmap(
                    progressBitmap,
                    progressCenterX - progressBitmap.width / 2.0f,
                    track.top - progressBitmap.height / 2.0f,
                    paint
                )
            }
        }
    }

    private fun drawMultipleThumb(canvas: Canvas) {
        progressThumbDrawable = CpnSeekbarDciThumbDrawable.getDrawable(
            context,
            CpnSeekbarDciThumbTypeDrawable.PROGRESS,
            progressValue.roundToInt()
        )

        val progressCenterX = progressTrack.right
        val progressBitmap = getDrawBitmap(progressThumbDrawable) ?: throw IllegalArgumentException(
            "Progress thumb drawable not found!"
        )
        canvas.drawBitmap(
            progressBitmap,
            progressCenterX - progressBitmap.width / 2.0f,
            progressTrack.top - progressBitmap.height / 2.0f,
            paint
        )
    }

    private fun drawTrack(canvas: Canvas) {
        /*
        * 1 Progress
        * 2 Minimum
        * 3 End
        * */

        // draw end track
        paint.color = backgroundTrackColor
        paint.strokeWidth = trackSize.toFloat()
        canvas.drawLine(
            backgroundTrack.left,
            backgroundTrack.top,
            backgroundTrack.right,
            backgroundTrack.bottom,
            paint
        )

        // draw minimum track
        paint.color = ResourcesCompat.getColor(resources, android.R.color.transparent, null)
        paint.strokeWidth = trackSize.toFloat()
        canvas.drawLine(
            minimumTrack.left,
            minimumTrack.top,
            minimumTrack.right,
            minimumTrack.bottom,
            paint
        )

        // draw progress track
        paint.color = progressTrackColor
        paint.strokeWidth = trackSize.toFloat()
        canvas.drawLine(
            progressTrack.left,
            progressTrack.top,
            progressTrack.right,
            progressTrack.bottom,
            paint
        )
    }

    private fun drawThumb(canvas: Canvas) {
        progressThumbDrawable = CpnSeekbarDciThumbDrawable.getDrawable(
            context,
            CpnSeekbarDciThumbTypeDrawable.PROGRESS,
            progressValue.roundToInt()
        )
        endThumbDrawable = CpnSeekbarDciThumbDrawable.getDrawable(
            context,
            CpnSeekbarDciThumbTypeDrawable.END,
            endValue.roundToInt()
        )

        /*
        * 1 End
        * 2 Minimum
        * 3 Progress
        * */

        val endCenterX = backgroundTrack.right
        val endBitmap = getDrawBitmap(endThumbDrawable)
            ?: throw IllegalArgumentException("End thumb drawable not found!")
        canvas.drawBitmap(
            endBitmap,
            endCenterX - endBitmap.width / 2.0f,
            backgroundTrack.top - endBitmap.height / 2.0f,
            paint
        )

        val minimumCenterX = minimumTrack.right
        val minimumBitmap = getDrawBitmap(minimumThumbDrawable)
            ?: throw IllegalArgumentException("Minimum thumb drawable not found!")
        canvas.drawBitmap(
            minimumBitmap,
            minimumCenterX - minimumBitmap.width / 2.0f,
            minimumTrack.top - minimumBitmap.height / 2.0f,
            paint
        )

        val progressCenterX = progressTrack.right
        val progressBitmap = getDrawBitmap(progressThumbDrawable) ?: throw IllegalArgumentException(
            "Progress thumb drawable not found!"
        )
        canvas.drawBitmap(
            progressBitmap,
            progressCenterX - progressBitmap.width / 2.0f,
            progressTrack.top - progressBitmap.height / 2.0f,
            paint
        )
    }

    private fun getDrawBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) return null
        var width: Int
        var height: Int
        val maxRange = CpnSeekbarDciSizeUtils.dp2px(context, THUMB_MAX_WIDTH)
        val intrinsicWidth = drawable.minimumWidth
        if (intrinsicWidth > maxRange) {
            width = thumbSize

            height = getHeightByRatio(drawable, width)
            if (height > maxRange) {
                width = maxRange
                height = getHeightByRatio(drawable, width)
            }
        } else {
            width = drawable.intrinsicWidth
            height = drawable.intrinsicHeight
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun getHeightByRatio(drawable: Drawable, width: Int): Int {
        val intrinsicWidth = drawable.intrinsicWidth
        val intrinsicHeight = drawable.intrinsicHeight
        return (1.0f * width * intrinsicHeight / intrinsicWidth).roundToInt()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        post {
            requestLayout()
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(BUNDLE_KEY, super.onSaveInstanceState())
        bundle.putFloat(PROGRESS_KEY, progressValue)
        bundle.putFloat(END_KEY, endValue)
        bundle.putFloat(START_KEY, startValue)
        bundle.putFloat(MINIMUM_KEY, minimumValue)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            val progress = state.getFloat(PROGRESS_KEY)
            updateProgress(progress)

            super.onRestoreInstanceState(state.getParcelable(BUNDLE_KEY))
            return
        }

        super.onRestoreInstanceState(state)
    }

    private fun updateProgress(progress: Float) {
        progressValue = if (progress < startValue) startValue else if (progress > endValue) endValue else progress
        refreshThumbPosition(progressValue)
        postInvalidate()
        updateStayIndicator()
    }

    private fun updateStayIndicator() {
        if (indicator == null) return
        indicatorView.measure(0, 0)

        val measuredWidth = indicatorView.measuredWidth
        val progressCenterX = progressTrack.right
        val indicatorOffset: Int
        val arrowOffset: Int
        if (measuredWidth / 2 + progressCenterX > measuredWidthSeekbar) {
            indicatorOffset = measuredWidthSeekbar - measuredWidth
            arrowOffset = (progressCenterX - indicatorOffset - measuredWidth / 2).toInt()
        } else if (progressCenterX - measuredWidth / 2 < 0) {
            indicatorOffset = 0
            arrowOffset = -(measuredWidth / 2 - progressCenterX).toInt()
        } else {
            indicatorOffset = (progressCenterX - measuredWidth / 2).toInt()
            arrowOffset = 0
        }

        indicator?.updateIndicatorLocation(indicatorOffset)
        indicator?.updateArrowLocation(arrowOffset)

    }

    fun showStayIndicator() {
        indicatorView.visibility = INVISIBLE
        postDelayed({
            updateStayIndicator()
            indicatorView.visibility = VISIBLE
        }, 300)
    }

    fun setIndicatorStayAlways(indicatorStayAlways: Boolean) {
        this.indicatorStayAlways = indicatorStayAlways
    }

    @Synchronized
    fun setLabelText(text: String) {
        indicator?.updateLabelText(text)
    }

    @Synchronized
    fun setProgressValue(value: Float) {
        progressValue = value
        updateProgress(progressValue)
    }

    @Synchronized
    fun setEndValue(value: Float) {
        endValue = value
        updateProgress(progressValue)
    }

    @Synchronized
    fun setProgressTrackColor(color: Int) {
        progressTrackColor = color
        postInvalidate()
    }

    @Synchronized
    fun setBackgroundTrackColor(color: Int) {
        backgroundTrackColor = color
        postInvalidate()
    }

    private var list = listOf<Int>()
    @Synchronized
    fun setList(list: List<Int>){
        this.list = list
        postInvalidate()
    }

    private companion object {
        const val THUMB_MAX_WIDTH = 30f

        const val BUNDLE_KEY = "cpn_seekbar_dic_bundle_key"
        const val PROGRESS_KEY = "cpn_seekbar_dic_progress_key"
        const val END_KEY = "cpn_seekbar_dic_end_key"
        const val START_KEY = "cpn_seekbar_dic_start_key"
        const val MINIMUM_KEY = "cpn_seekbar_dic_minimum_key"
    }
}