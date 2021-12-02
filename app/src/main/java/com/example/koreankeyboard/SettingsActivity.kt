package com.example.koreankeyboard

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.widget.ImageView
import android.widget.Toast
import com.example.koreankeyboard.classes.Misc
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.ByteArrayOutputStream


@SuppressLint("ExportedPreferenceActivity")
class SettingsActivity : PreferenceActivity(),
    OnSharedPreferenceChangeListener {

    private lateinit var dialog: Dialog
    private val requestCode = 1243
    private val actionRequestGallery = 123

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        addPreferencesFromResource(R.xml.root_preferences)
        PreferenceManager.getDefaultSharedPreferences(this)

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, str: String) {
        Toast.makeText(applicationContext, "Auto completion checkbox ", Toast.LENGTH_LONG).show()
        when (str) {
            PREDICT -> {
                val z2 = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                    PREDICT, true
                )
                val applicationContext2 = applicationContext
                Toast.makeText(applicationContext2, "Prediction checkbox is $z2", Toast.LENGTH_LONG)
                    .show()
            }
            VIBRATE -> {
                val z3 = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                    VIBRATE, true
                )
                val applicationContext3 = applicationContext
                Toast.makeText(applicationContext3, "Vibration checkbox is $z3", Toast.LENGTH_LONG)
                    .show()
            }
            SOUND -> {
                val z4 = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                    SOUND, true
                )
                val applicationContext4 = applicationContext
                Toast.makeText(applicationContext4, "Sound checkbox is $z4", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, resultUri)
                dialog.findViewById<ImageView>(R.id.theme_a).setImageBitmap(bitmap)

                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos) //bm is the bitmap object

                val b: ByteArray = baos.toByteArray()
                val encoded: String = Base64.encodeToString(b, Base64.DEFAULT)

                val sharedPreferences =
                    getSharedPreferences(Misc.themeFromGallery, AppCompatActivity.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(Misc.themeFromGallery, encoded)
                editor.apply()

                Misc.setTheme(this, 198)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this, "Some error occurred in image cropping.", Toast.LENGTH_LONG)
                    .show()
                error.printStackTrace()
            }
        }
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                actionRequestGallery -> {
                    val galleryImageUri: Uri? = data?.data
                    CropImage.activity(galleryImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);

                }

            }
        }
    }

    companion object {
        const val AUTO = "auto"
        const val KEYPREVIEW = "key_preview"
        const val PREDICT = "predict"
        const val SOUND = "sounds"
        const val VIBRATE = "vibration"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == requestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, "Choose a Picture")
            startActivityForResult(chooser, actionRequestGallery)
        }
    }
}

