package com.example.koreankeyboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.koreankeyboard.classes.Misc
import com.example.koreankeyboard.databinding.ActivityHowToUseBinding

class HowToUseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHowToUseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHowToUseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Misc.initTopBar(this, "How to use")
    }
}