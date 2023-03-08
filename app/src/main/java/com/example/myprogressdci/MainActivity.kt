package com.example.myprogressdci

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.HorizontalScrollView
import com.example.myprogressdci.seekbar.CpnSeekbarDci
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val progress = 15f

        val dci = findViewById<CpnSeekbarDci>(R.id.dci_progress)
        val hSv = findViewById<HorizontalScrollView>(R.id.horizontal_sv)

        val listProgress = listOf(4f, 8f, 12f, 16f, 20f, 24f, 28f)

        hSv.viewTreeObserver.addOnGlobalLayoutListener {
            var currentProgressValue = 0f
            val contentWidth = hSv.getChildAt(0).width
            val itemLength = (contentWidth / listProgress.size).toFloat()
            val halfOfItem = (itemLength / 2)
            val resultList = arrayListOf<Float>()
            var nextChain = halfOfItem
            val distance = resources.getDimension(R.dimen.item_margin)
            var currentChain = 0f
            var beforeProgress = 0f
            listProgress.forEach { currentSelectedProgress ->
                if (currentProgressValue == 0f) {
                    if (currentSelectedProgress >= progress) {
                        currentProgressValue = if (progress == currentSelectedProgress) {
                            nextChain + distance
                        } else {
                            val nextOfChain = nextChain + distance
                            val distanceWithNextProgress = currentSelectedProgress - beforeProgress
                            val resultDistance =
                                (progress - beforeProgress) / distanceWithNextProgress * 100
                            val totalLengthWithNextChain = nextOfChain - currentChain
                            val resultLength = resultDistance / 100 * totalLengthWithNextChain
                            currentChain + resultLength
                        }
                    }
                }
                currentChain = (nextChain + distance)
                resultList.add(currentChain)
                nextChain += itemLength
                beforeProgress = currentSelectedProgress
            }

            dci.setLabelText("Stamp progress", progress.roundToInt())
            dci.setProgressValue(currentProgressValue)
            dci.setEndValue(contentWidth.toFloat())
            dci.setList(resultList.toList())
            dci.setProgressList(listProgress.toList())
        }
    }
}