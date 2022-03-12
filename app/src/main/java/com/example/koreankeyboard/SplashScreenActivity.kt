package com.example.koreankeyboard

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.example.koreankeyboard.databinding.ActivitySplashScreenBinding
import com.example.koreankeyboard.services.CustomInputMethodService
import com.example.koreankeyboard.MainActivity
import com.example.koreankeyboard.classes.Misc

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.extras?.get(Misc.logKey) != null){
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(Misc.logKey, Misc.logKey)
            startActivity(intent)
            finish()
        }

        Misc.downloadTranslationModel(this)


        binding.btnStart.setOnClickListener {
            if(isInputMethodSelected()){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this, EnableKeyboardActivity::class.java))
                finish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun isInputMethodSelected(): Boolean {
        val id: String = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.DEFAULT_INPUT_METHOD
        )
        val defaultInputMethod = ComponentName.unflattenFromString(id)
        val myInputMethod = ComponentName(this, CustomInputMethodService::class.java)
        return myInputMethod == defaultInputMethod
    }

}