package com.example.koreankeyboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_how_to_use.*

class HowToUseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_use)

        btnBackHowToUse.setOnClickListener {
            onBackPressed()
        }
    }
}