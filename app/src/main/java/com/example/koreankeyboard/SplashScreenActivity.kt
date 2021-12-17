package com.example.koreankeyboard

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.example.koreankeyboard.databinding.ActivitySplashScreenBinding
import com.example.koreankeyboard.services.CustomInputMethodService

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnStart.setOnClickListener {
            if(isInputMethodSelected()){
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                finish()
                startActivity(Intent(this, EnableKeyboardActivity::class.java))
            }
        }
    }

    fun isInputMethodSelected(): Boolean {
        val id: String = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.DEFAULT_INPUT_METHOD
        )
        val defaultInputMethod = ComponentName.unflattenFromString(id)
        val myInputMethod = ComponentName(this, CustomInputMethodService::class.java)
        return myInputMethod == defaultInputMethod
    }

}