package com.example.koreankeyboard

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.koreankeyboard.classes.Misc
import com.example.koreankeyboard.databinding.ActivitySettingsBinding


@SuppressLint("ExportedPreferenceActivity")
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.cbSound.isChecked = Misc.getIsSettingEnable(this, Misc.sound)
        binding.cbVibration.isChecked = Misc.getIsSettingEnable(this, Misc.vibrate)
        binding.cbSuggestions.isChecked = Misc.getIsSettingEnable(this, Misc.suggestions)

        binding.cbSound.setOnCheckedChangeListener { _, isChecked ->
            Misc.setIsSettingEnable(this, isChecked,Misc.sound)
        }
        binding.cbVibration.setOnCheckedChangeListener { _, isChecked ->
            Misc.setIsSettingEnable(this, isChecked,Misc.vibrate)
        }
        binding.cbSuggestions.setOnCheckedChangeListener { _, isChecked ->
            Misc.setIsSettingEnable(this, isChecked,Misc.suggestions)
        }

        Misc.InitTopBar(this, "Settings")
    }

}

