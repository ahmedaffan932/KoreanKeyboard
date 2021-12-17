package com.example.koreankeyboard

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.koreankeyboard.databinding.ActivityEnableKeyboardBinding
import com.example.koreankeyboard.services.CustomInputMethodService
import java.util.*

class EnableKeyboardActivity : AppCompatActivity() {
    private val t = Timer()
    private lateinit var binding: ActivityEnableKeyboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnableKeyboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clEnableKeyboard.setOnClickListener {
            startActivityForResult(Intent("android.settings.INPUT_METHOD_SETTINGS"), 0)
        }

        t.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    if (isInputMethodSelected()) {
                        finish()
                        binding.clSelectKeyboard.background =
                            resources.getDrawable(R.drawable.bg_main_less_rounded)
                        binding.textSelect.setTextColor(Color.WHITE)
                        startActivity(Intent(this@EnableKeyboardActivity, MainActivity::class.java))
                    } else {
                        binding.clSelectKeyboard.background =
                            resources.getDrawable(R.drawable.bg_plain_less_rounded)
                    }
                }
            },
            100,
            1000
        )
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            checkKeyboardActivation()
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

    override fun onDestroy() {
        super.onDestroy()
        t.cancel()
    }

    override fun onResume() {
        super.onResume()
        checkKeyboardActivation()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun checkKeyboardActivation() {
        //Check Keyboard Enabled
        val packageLocal = packageName

        //InputMethodManager
        val mInputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val lists: String = mInputMethodManager.enabledInputMethodList.toString()
        val mActKeyboard = lists.contains(packageLocal)

        if (mActKeyboard) {
            binding.clEnableKeyboard.setOnClickListener { }
//            btnSelectKeyBoard.visibility = View.VISIBLE
            binding.clEnableKeyboard.background =
                resources.getDrawable(R.drawable.bg_main_less_rounded)
            binding.textEnable.setTextColor(resources.getColor(R.color.white))
            binding.clMain.background = resources.getDrawable(R.drawable.bg_select)

            binding.clSelectKeyboard.setOnClickListener {
                (getSystemService(
                    getString(R.string.input_method)
                ) as InputMethodManager).showInputMethodPicker()
            }
        }
    }
}

