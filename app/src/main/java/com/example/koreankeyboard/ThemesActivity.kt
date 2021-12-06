package com.example.koreankeyboard

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Base64
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.koreankeyboard.classes.Misc
import com.rw.keyboardlistener.KeyboardUtils
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.keyboard_themes_dialog.*
import java.io.ByteArrayOutputStream

@RequiresApi(Build.VERSION_CODES.M)
class ThemesActivity : AppCompatActivity() {
    private val requestCode = 1243
    private val actionRequestGallery = 123
    lateinit var previousThemeView: ImageView
    var isKeyboardOpen = false

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.keyboard_themes_dialog)

        KeyboardUtils.addKeyboardToggleListener(this){ isVisible ->
            isKeyboardOpen = isVisible
        }

        selectedTheme()

        theme_a.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
                    val chooser = Intent.createChooser(intent, "Choose a Picture")
                    startActivityForResult(chooser, actionRequestGallery)
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        requestCode
                    );
                }
            } else {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                val chooser = Intent.createChooser(intent, "Choose a Picture")
                startActivityForResult(chooser, actionRequestGallery)
            }

        }
        theme_b.setOnClickListener {
            Misc.setTheme(this, 1)
            openKeyboard()
        }
        theme_c.setOnClickListener {
            Misc.setTheme(this, 2)
            openKeyboard()

        }
        theme_d.setOnClickListener {
            Misc.setTheme(this, 3)
            openKeyboard()

        }
        theme_e.setOnClickListener {
            Misc.setTheme(this, 4)
            openKeyboard()

        }
        theme_f.setOnClickListener {
            Misc.setTheme(this, 5)

            openKeyboard()
        }
        theme_g.setOnClickListener {
            Misc.setTheme(this, 6)
            openKeyboard()

        }
        theme_h.setOnClickListener {
            Misc.setTheme(this, 7)

            openKeyboard()
        }
        theme_i.setOnClickListener {
            Misc.setTheme(this, 8)

            openKeyboard()
        }
        theme_j.setOnClickListener {
            Misc.setTheme(this, 9)
            openKeyboard()

        }
        theme_k.setOnClickListener {
            Misc.setTheme(this, 10)

            openKeyboard()
        }
        theme_l.setOnClickListener {
            Misc.setTheme(this, 11)
            openKeyboard()
        }
        theme_m.setOnClickListener {
            Misc.setTheme(this, 12)
            openKeyboard()
        }
        theme_n.setOnClickListener {
            Misc.setTheme(this, 13)
            openKeyboard()

        }
        theme_o.setOnClickListener {
            Misc.setTheme(this, 14)
            openKeyboard()

        }
        theme_p.setOnClickListener {
            Misc.setTheme(this, 15)
            openKeyboard()

        }
        theme_q.setOnClickListener {
            Misc.setTheme(this, 16)
            openKeyboard()

        }
        theme_r.setOnClickListener {
            Misc.setTheme(this, 17)
            openKeyboard()
        }
        theme_s.setOnClickListener {
            Misc.setTheme(this, 18)
            openKeyboard()
        }
        theme_t.setOnClickListener {
            Misc.setTheme(this, 19)
            openKeyboard()
        }
        theme_u.setOnClickListener {
            Misc.setTheme(this, 20)
            openKeyboard()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, resultUri)
                theme_a.setImageBitmap(bitmap)

                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos) //bm is the bitmap object

                val b: ByteArray = baos.toByteArray()
                val encoded: String = Base64.encodeToString(b, Base64.DEFAULT)

                val sharedPreferences =
                    getSharedPreferences(Misc.themeFromGallery, MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(Misc.themeFromGallery, encoded)
                editor.apply()

                Misc.setTheme(this, 0)
                openKeyboard()

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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun openKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        val mDrawableTheme = resources.getDrawable(R.drawable.bg_nothing)
        previousThemeView.foreground = mDrawableTheme
        if (isKeyboardOpen)
            Handler().postDelayed({
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }, 300)

        isKeyboardOpen = true
        selectedTheme()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, "Choose a Picture")
            startActivityForResult(chooser, actionRequestGallery)
        }
    }

    private fun selectedTheme() {

        val th = Misc.getTheme(this)
        val mDrawableTheme = resources.getDrawable(R.drawable.done)
        previousThemeView = when (th) {
            0 -> {
                theme_a.foreground = mDrawableTheme
                theme_a
            }
            1 -> {
                theme_b.foreground = mDrawableTheme
                theme_b
            }
            2 -> {
                theme_c.foreground = mDrawableTheme
                theme_c
            }
            3 -> {
                theme_d.foreground = mDrawableTheme
                theme_d
            }
            4 -> {
                theme_e.foreground = mDrawableTheme
                theme_e
            }
            5 -> {
                theme_f.foreground = mDrawableTheme
                theme_f
            }
            6 -> {
                theme_g.foreground = mDrawableTheme
                theme_g
            }
            7 -> {
                theme_h.foreground = mDrawableTheme
                theme_h
            }
            8 -> {
                theme_i.foreground = mDrawableTheme
                theme_i
            }
            9 -> {
                theme_j.foreground = mDrawableTheme
                theme_j
            }
            10 -> {
                theme_k.foreground = mDrawableTheme
                theme_k
            }
            11 -> {
                theme_l.foreground = mDrawableTheme
                theme_l
            }
            12 -> {
                theme_m.foreground = mDrawableTheme
                theme_m
            }
            13 -> {
                theme_n.foreground = mDrawableTheme
                theme_n
            }
            14 -> {
                theme_o.foreground = mDrawableTheme
                theme_o
            }
            15 -> {
                theme_p.foreground = mDrawableTheme
                theme_p
            }
            16 -> {
                theme_q.foreground = mDrawableTheme
                theme_q
            }
            17 -> {
                theme_r.foreground = mDrawableTheme
                theme_r
            }
            18 -> {
                theme_s.foreground = mDrawableTheme
                theme_s
            }
            19 -> {
                theme_t.foreground = mDrawableTheme
                theme_t
            }
            else -> {
                theme_u.foreground = mDrawableTheme
                theme_u
            }
        }
    }
}