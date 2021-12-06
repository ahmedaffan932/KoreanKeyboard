package com.example.koreankeyboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_how_to_use.*
import kotlinx.android.synthetic.main.activity_how_to_use.btnExit
import kotlinx.android.synthetic.main.activity_main.*

class HowToUseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_use)

        btnBackHowToUse.setOnClickListener {
            onBackPressed()
        }

        btnExit.setOnClickListener {
            Toast.makeText(this, "Enjoy Korean keyboard.", Toast.LENGTH_SHORT).show()
            finishAffinity()
        }
    }
}