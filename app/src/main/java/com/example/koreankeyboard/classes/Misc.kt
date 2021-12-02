package com.example.koreankeyboard.classes

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.BuildConfig

class Misc {
    companion object {
        const val themeFromGallery: String = "themeFromGallery"
        const val data: String = "data"
        const val logKey = "logKey"
        private const val themeKey = "themeKey"
        private const val selectedKeyboard = "selectedKeyboard"
        private const val kyeBackground = "kyeBackground"
        const val appUrl: String =
            "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"

        fun extendedLogKey(string: String): String {
            return "logKey $string"
        }

        fun getTheme(context: Context): Int {
            val sharedPreferences =
                context.getSharedPreferences(themeKey, Context.MODE_PRIVATE)
            return sharedPreferences.getInt(themeKey, 0)
        }

        fun setTheme(context: Context) {
            val sharedPreferences =
                context.getSharedPreferences(themeKey, AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            if (getTheme(context) > 20) {
                editor.putInt(themeKey, 0)
            } else {
                editor.putInt(themeKey, getTheme(context) + 1)
            }
            editor.apply()
        }

        fun setTheme(context: Context, themeId: Int) {
            val sharedPreferences =
                context.getSharedPreferences(themeKey, AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt(themeKey, themeId)
            editor.apply()
        }

        fun setIsKorean(context: Context, isKorean: Boolean) {
            val sharedPreferences =
                context.getSharedPreferences(selectedKeyboard, AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean(selectedKeyboard, isKorean)
            editor.apply()
        }


        fun getIsKorean(context: Context): Boolean {
            val sharedPreferences =
                context.getSharedPreferences(selectedKeyboard, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(selectedKeyboard, false)

        }

        fun isKeyBackgroundEnable(context: Context): Boolean {
            val sharedPreferences =
                context.getSharedPreferences(kyeBackground, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(kyeBackground, true)
        }

        fun setIsKeyBackgroundEnable(context: Context, isKeyBackground: Boolean) {
            val sharedPreferences =
                context.getSharedPreferences(kyeBackground, AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean(kyeBackground, isKeyBackground)
            editor.apply()
        }
    }
}