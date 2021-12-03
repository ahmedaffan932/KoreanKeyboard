package com.example.koreankeyboard

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.koreankeyboard.classes.Misc
import com.example.koreankeyboard.services.CustomInputMethodService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var isMenuOpen = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clMenu.setOnClickListener { }

        btnSelectKeyBoard.setOnClickListener {
            startActivityForResult(Intent("android.settings.INPUT_METHOD_SETTINGS"), 0)
        }

        btnEnableKeyBoard.setOnClickListener {
            (getSystemService(
                getString(R.string.input_method)
            ) as InputMethodManager).showInputMethodPicker()
        }

        btnExit.setOnClickListener {
            finishAffinity()
        }

        btnPrivacyPolicy.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://applogixpro.blogspot.com/2021/10/privacy-policy-app-logix-developer.html")
            )
            startActivity(intent)
        }

        btnMoreApps.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/developer?id=app+Logix+Developer")
            )
            startActivity(intent)
        }

        btnRateUs.setOnClickListener {
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

        btnShareApp.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Have a look to this interesting application:- \n \n${Misc.appUrl}"
            )
            startActivity(Intent.createChooser(sharingIntent, "Share via"))
        }

        blockView.setOnClickListener {
            blockView.visibility = View.GONE
            clMenu.animate().translationX(-clMenu.width.toFloat())
            clMain.animate().translationX(0f)

//            val a: Animation =
//                AnimationUtils.loadAnimation(
//                    this,
//                    R.anim.slide_from_right_to_left
//                )
//            a.fillAfter = true
//            clMain.startAnimation(a)
            isMenuOpen = false
        }

        btnMenu.setOnClickListener {
            clMenu.animate().translationX(0f)
            clMain.animate().translationX(clMenu.width.toFloat())
            blockView.visibility = View.VISIBLE

//            val a: Animation =
//                AnimationUtils.loadAnimation(
//                    this,
//                    R.anim.slide_from_left_to_right
//                )
//            a.fillAfter = true
//            clMain.startAnimation(a)
            isMenuOpen = true
        }

        btnMenuClose.setOnClickListener {
            blockView.visibility = View.GONE
            clMenu.animate().translationX(-clMenu.width.toFloat())
            clMain.animate().translationX(0f)

//            val a: Animation =
//                AnimationUtils.loadAnimation(
//                    this,
//                    R.anim.slide_from_right_to_left
//                )
//            a.fillAfter = true
//            clMain.startAnimation(a)

            isMenuOpen = false
        }

        clSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        clTheme.setOnClickListener {
            startActivity(Intent(this, ThemesActivity::class.java))
        }

        btnHowToUse.setOnClickListener {
            startActivity(Intent(this, HowToUseActivity::class.java))
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

    override fun onBackPressed() {
        if (isMenuOpen) {
            blockView.visibility = View.GONE
            clMenu.animate().translationX(-clMenu.width.toFloat())
            clMain.animate().translationX(0f)

//            val a: Animation =
//                AnimationUtils.loadAnimation(
//                    this,
//                    R.anim.slide_from_right_to_left
//                )
//            a.fillAfter = true
//            clMain.startAnimation(a)
            isMenuOpen = false
        } else {
            super.onBackPressed()
        }
    }
}