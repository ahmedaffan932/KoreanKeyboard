package com.example.koreankeyboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.koreankeyboard.classes.Misc
import com.example.koreankeyboard.databinding.ActivitySettingsBinding
import com.example.koreankeyboard.interfaces.NativeAdCallBack


@SuppressLint("ExportedPreferenceActivity")
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private var temp = false

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cbSound.isChecked = Misc.getIsSettingEnable(this, Misc.sound)
        binding.cbVibration.isChecked = Misc.getIsSettingEnable(this, Misc.vibrate)
        binding.cbSuggestions.isChecked = Misc.getIsSettingEnable(this, Misc.suggestions)

        binding.kbSizeLarge.isChecked = Misc.getIsKeyboardSizeLarge(this)
        binding.kbSizeSmall.isChecked = !Misc.getIsKeyboardSizeLarge(this)


        Misc.loadNativeAd(this, object : NativeAdCallBack{
            override fun onLoad() {
                Misc.showNativeAd(binding.nativeAdViewMain)
                binding.nativeAdViewMain.visibility = View.VISIBLE
            }
        })
        binding.kbSizeSmall.setOnCheckedChangeListener { _, isChecked ->
            Misc.setIsKeyboardSizeLarge(this, !isChecked)
            triggerRebirth(this)
        }

        binding.kbSizeLarge.setOnCheckedChangeListener { _, isChecked ->
            Misc.setIsKeyboardSizeLarge(this, isChecked)
            triggerRebirth(this)
        }

        binding.cbSound.setOnCheckedChangeListener { _, isChecked ->
            Misc.setIsSettingEnable(this, isChecked,Misc.sound)
        }
        binding.cbVibration.setOnCheckedChangeListener { _, isChecked ->
            Misc.setIsSettingEnable(this, isChecked,Misc.vibrate)
        }
        binding.cbSuggestions.setOnCheckedChangeListener { _, isChecked ->
            Misc.setIsSettingEnable(this, isChecked,Misc.suggestions)
        }

        Misc.initTopBar(this, "Settings")
    }

    private fun triggerRebirth(context: Context) {

        Handler().postDelayed({
            val packageManager: PackageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(context.packageName)
            val componentName = intent!!.component
            val mainIntent = Intent.makeRestartActivityTask(componentName)
            overridePendingTransition( 0, 0);
            mainIntent.putExtra(Misc.logKey, Misc.logKey)
            context.startActivity(mainIntent)
            Runtime.getRuntime().exit(0)

//            val mStartActivity = Intent(context, MainActivity::class.java)
//            val mPendingIntentId = 123456
//
//            mStartActivity.putExtra(Misc.logKey, Misc.logKey)
//
//            val mPendingIntent = PendingIntent.getActivity(
//                context,
//                mPendingIntentId,
//                mStartActivity,
//                PendingIntent.FLAG_CANCEL_CURRENT
//            )
//            val mgr = context.getSystemService(ALARM_SERVICE) as AlarmManager
//            mgr[AlarmManager.RTC, System.currentTimeMillis() + 1] = mPendingIntent
//            exitProcess(0) //you can also kill your app's process
        }, 100)
    }

}

