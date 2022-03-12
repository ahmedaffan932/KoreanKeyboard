package com.example.koreankeyboard

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.koreankeyboard.classes.Misc
import com.example.koreankeyboard.databinding.ActivityMainBinding
import com.example.koreankeyboard.interfaces.InterstitialCallBack
import com.rw.keyboardlistener.KeyboardUtils
import com.rw.keyboardlistener.KeyboardUtils.SoftKeyboardToggleListener


class MainActivity : AppCompatActivity() {
    private var isMenuOpen = false
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.clMenu.setOnClickListener { }

        KeyboardUtils.addKeyboardToggleListener(this
        ) { isVisible ->
            if (isVisible) {
                binding.unFocusView.visibility = View.VISIBLE
            } else {
                binding.unFocusView.visibility = View.GONE
            }
        }

        Misc.loadBannerAd(this, binding.frameLayoutBannerAd)

        Misc.showInterstitial(this, null)

        if (intent.getStringExtra(Misc.logKey) != null) {
            binding.et.requestFocus()
            Handler().postDelayed({
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.et, InputMethodManager.SHOW_IMPLICIT)
            }, 200)
        }

        binding.et.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.unFocusView.visibility = View.VISIBLE
            } else {
                binding.unFocusView.visibility = View.GONE
            }
        }

        binding.unFocusView.setOnClickListener {
            val view = this.currentFocus
            if (view != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            binding.et.clearFocus();
        }
        binding.btnSelectKeyBoard.setOnClickListener {
            startActivityForResult(Intent("android.settings.INPUT_METHOD_SETTINGS"), 0)
        }

        binding.btnEnableKeyBoard.setOnClickListener {
            (getSystemService(
                getString(R.string.input_method)
            ) as InputMethodManager).showInputMethodPicker()
        }

        binding.btnExit.setOnClickListener {
            finishAffinity()
        }

        binding.btnPrivacyPolicy.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://applogixpro.blogspot.com/2021/10/privacy-policy-app-logix-developer.html")
            )
            startActivity(intent)
        }

        binding.btnMoreApps.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/developer?id=app+Logix+Developer")
            )
            startActivity(intent)
        }

        binding.btnRateUs.setOnClickListener {
            val p = BuildConfig.APPLICATION_ID
            val uri: Uri = Uri.parse("market://details?id=$p")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=$p")
                    )
                )
            }
        }

        binding.btnShareApp.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Have a look to this interesting application:- \n \n${Misc.appUrl}"
            )
            startActivity(Intent.createChooser(sharingIntent, "Share via"))
        }

        binding.blockView.setOnClickListener {
            onBackPressed()
        }

        binding.btnMenu.setOnClickListener {
            if (isMenuOpen) {
                onBackPressed()
            } else {
                binding.clMenu.animate().translationX(0f)
                binding.clMain.animate().translationX(binding.clMenu.width.toFloat())
                binding.blockView.visibility = View.VISIBLE
                isMenuOpen = true

            }
        }

        binding.btnMenuClose.setOnClickListener {
            onBackPressed()
        }

        binding.clSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.clTheme.setOnClickListener {
            Misc.showInterstitial(this, object : InterstitialCallBack{
                @RequiresApi(Build.VERSION_CODES.M)
                override fun onDismiss() {
                    startActivity(Intent(this@MainActivity, ThemesActivity::class.java))
                }
            })
        }

        binding.btnHowToUse.setOnClickListener {
            Misc.showInterstitial(this, object :InterstitialCallBack{
                override fun onDismiss() {
                    startActivity(Intent(this@MainActivity, HowToUseActivity::class.java))
                }
            })
        }
    }

    override fun onBackPressed() {
        if (isMenuOpen) {
            binding.blockView.visibility = View.GONE
            binding.clMenu.animate().translationX(-binding.clMenu.width.toFloat())
            binding.clMain.animate().translationX(0f)

            isMenuOpen = false
        } else {
            finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()
        KeyboardUtils.addKeyboardToggleListener(
            this
        ) { isVisible ->
            if(isVisible){
                binding.unFocusView.visibility = View.VISIBLE
            }else{
                binding.unFocusView.visibility = View.GONE
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.et.clearFocus()
    }
}