package com.example.myprogressdci

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myprogressdci.seekbar.CpnSeekbarDci
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val progress = 16f

        val dci = findViewById<CpnSeekbarDci>(R.id.dci_progress)
        dci.setLabelText("Stamp progress ${progress.roundToInt()}")
        dci.setProgressValue(progress)
        dci.setEndValue(28f)
        dci.setList(listOf(4, 8, 12, 16, 20, 24, 28))
    }
}