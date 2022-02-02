package com.example.koreankeyboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.koreankeyboard.classes.Misc
import com.example.koreankeyboard.databinding.ActivityAllDoneBinding

class AllDoneActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllDoneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllDoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLetsGo.setOnClickListener {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }

        Handler().postDelayed({
            Misc.zoomInView(binding.textView3, this, 250)
            Handler().postDelayed({
                Misc.zoomInView(binding.tvKbSelected, this, 250)
                Handler().postDelayed({
                    Misc.zoomInView(binding.tvThemes, this, 250)
                    Handler().postDelayed({
                        Misc.zoomInView(binding.btnLetsGo, this, 250)
                    }, 500)
                }, 500)
            }, 500)
        }, 500)

    }
}