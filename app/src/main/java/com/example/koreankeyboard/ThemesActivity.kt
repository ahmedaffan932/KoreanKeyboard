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
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.koreankeyboard.classes.Misc
import com.example.koreankeyboard.databinding.KeyboardThemesDialogBinding
import com.rw.keyboardlistener.KeyboardUtils
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.ByteArrayOutputStream

@RequiresApi(Build.VERSION_CODES.M)
class ThemesActivity : AppCompatActivity() {
    private val requestCode = 1243
    private val actionRequestGallery = 123
    lateinit var previousThemeView: ImageView
    var isKeyboardOpen = false
    private lateinit var binding: KeyboardThemesDialogBinding

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = KeyboardThemesDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Misc.initTopBar(this, "Themes")

        KeyboardUtils.addKeyboardToggleListener(this){ isVisible ->
            isKeyboardOpen = isVisible
        }

        selectedTheme()

        binding.themeA.setOnClickListener {
            if(isKeyboardOpen) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }

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
        binding.themeB.setOnClickListener {
            Misc.setTheme(this, 1)
            openKeyboard()
        }
        binding.themeC.setOnClickListener {
            Misc.setTheme(this, 2)
            openKeyboard()

        }
        binding.themeD.setOnClickListener {
            Misc.setTheme(this, 3)
            openKeyboard()

        }
        binding.themeE.setOnClickListener {
            Misc.setTheme(this, 4)
            openKeyboard()

        }
        binding.themeF.setOnClickListener {
            Misc.setTheme(this, 5)

            openKeyboard()
        }
        binding.themeG.setOnClickListener {
            Misc.setTheme(this, 6)
            openKeyboard()

        }
        binding.themeH.setOnClickListener {
            Misc.setTheme(this, 7)

            openKeyboard()
        }
        binding.themeI.setOnClickListener {
            Misc.setTheme(this, 8)
            openKeyboard()
        }
        binding.themeJ.setOnClickListener {
            Misc.setTheme(this, 9)
            openKeyboard()

        }
        binding.themeK.setOnClickListener {
            Misc.setTheme(this, 10)

            openKeyboard()
        }
        binding.themeL.setOnClickListener {
            Misc.setTheme(this, 11)
            openKeyboard()
        }
        binding.themeM.setOnClickListener {
            Misc.setTheme(this, 12)
            openKeyboard()
        }
        binding.themeN.setOnClickListener {
            Misc.setTheme(this, 13)
            openKeyboard()

        }
        binding.themeO.setOnClickListener {
            Misc.setTheme(this, 14)
            openKeyboard()

        }
        binding.themeP.setOnClickListener {
            Misc.setTheme(this, 15)
            openKeyboard()

        }
        binding.themeQ.setOnClickListener {
            Misc.setTheme(this, 16)
            openKeyboard()

        }
        binding.themeR.setOnClickListener {
            Misc.setTheme(this, 17)
            openKeyboard()
        }
        binding.themeS.setOnClickListener {
            Misc.setTheme(this, 18)
            openKeyboard()
        }
        binding.themeT.setOnClickListener {
            Misc.setTheme(this, 19)
            openKeyboard()
        }
        binding.themeU.setOnClickListener {
            Misc.setTheme(this, 20)
            openKeyboard()
        }
        binding.themeG1.setOnClickListener {
            Misc.setTheme(this, 21)
            openKeyboard()
        }
        binding.themeG2.setOnClickListener {
            Misc.setTheme(this, 22)
            openKeyboard()
        }
        binding.themeG3.setOnClickListener {
            Misc.setTheme(this, 23)
            openKeyboard()
        }
        binding.themeG4.setOnClickListener {
            Misc.setTheme(this, 24)
            openKeyboard()
        }
        binding.themeG5.setOnClickListener {
            Misc.setTheme(this, 25)
            openKeyboard()
        }
        binding.themeG6.setOnClickListener {
            Misc.setTheme(this, 26)
            openKeyboard()
        }
        binding.themeG7.setOnClickListener {
            Misc.setTheme(this, 27)
            openKeyboard()
        }
        binding.themeG8.setOnClickListener {
            Misc.setTheme(this, 28)
            openKeyboard()
        }
        binding.themeG9.setOnClickListener {
            Misc.setTheme(this, 29)
            openKeyboard()
        }
        binding.themeG10.setOnClickListener {
            Misc.setTheme(this, 30)
            openKeyboard()
        }
        binding.themeG11.setOnClickListener {
            Misc.setTheme(this, 31)
            openKeyboard()
        }
        binding.themeG12.setOnClickListener {
            Misc.setTheme(this, 32)
            openKeyboard()
        }
        binding.themeG13.setOnClickListener {
            Misc.setTheme(this, 33)
            openKeyboard()
        }
        binding.themeG14.setOnClickListener {
            Misc.setTheme(this, 34)
            openKeyboard()
        }
        binding.themeG15.setOnClickListener {
            Misc.setTheme(this, 35)
            openKeyboard()
        }
        binding.themeG16.setOnClickListener {
            Misc.setTheme(this, 36)
            openKeyboard()
        }
        binding.themeG17.setOnClickListener {
            Misc.setTheme(this, 37)
            openKeyboard()
        }
        binding.themeG18.setOnClickListener {
            Misc.setTheme(this, 38)
            openKeyboard()
        }
        binding.themeG19.setOnClickListener {
            Misc.setTheme(this, 39)
            openKeyboard()
        }
        binding.themeG20.setOnClickListener {
            Misc.setTheme(this, 40)
            openKeyboard()
        }
        binding.themeG21.setOnClickListener {
            Misc.setTheme(this, 41)
            openKeyboard()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            binding.pb.visibility = View.VISIBLE
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                object : Thread() {
                    override fun run() {
                        val l = Looper.getMainLooper()
                        val h = Handler(l)
                        h.post {
                            val resultUri = result.uri
                            val bitmap = MediaStore.Images.Media.getBitmap(this@ThemesActivity.contentResolver, resultUri)
                            binding.themeA.setImageBitmap(bitmap)

                            val baos = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos) //bm is the bitmap object

                            val b: ByteArray = baos.toByteArray()
                            val encoded: String = Base64.encodeToString(b, Base64.DEFAULT)

                            val sharedPreferences =
                                getSharedPreferences(Misc.themeFromGallery, MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString(Misc.themeFromGallery, encoded)
                            editor.apply()

                            Misc.setTheme(this@ThemesActivity, 0)
                            binding.pb.visibility = View.GONE
                            openKeyboard()
                        }
                    }
                }.start()

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this, "Some error occurred in image cropping.", Toast.LENGTH_LONG)
                    .show()
                error.printStackTrace()
                binding.pb.visibility = View.GONE
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
                binding.themeA.foreground = mDrawableTheme
                binding.themeA
            }
            1 -> {
                binding.themeB.foreground = mDrawableTheme
                binding.themeB
            }
            2 -> {
                binding.themeC.foreground = mDrawableTheme
                binding.themeC
            }
            3 -> {
                binding.themeD.foreground = mDrawableTheme
                binding.themeD
            }
            4 -> {
                binding.themeE.foreground = mDrawableTheme
                binding.themeE
            }
            5 -> {
                binding.themeF.foreground = mDrawableTheme
                binding.themeF
            }
            6 -> {
                binding.themeG.foreground = mDrawableTheme
                binding.themeG
            }
            7 -> {
                binding.themeH.foreground = mDrawableTheme
                binding.themeH
            }
            8 -> {
                binding.themeI.foreground = mDrawableTheme
                binding.themeI
            }
            9 -> {
                binding.themeJ.foreground = mDrawableTheme
                binding.themeJ
            }
            10 -> {
                binding.themeK.foreground = mDrawableTheme
                binding.themeK
            }
            11 -> {
                binding.themeL.foreground = mDrawableTheme
                binding.themeL
            }
            12 -> {
                binding.themeM.foreground = mDrawableTheme
                binding.themeM
            }
            13 -> {
                binding.themeN.foreground = mDrawableTheme
                binding.themeN
            }
            14 -> {
                binding.themeO.foreground = mDrawableTheme
                binding.themeO
            }
            15 -> {
                binding.themeP.foreground = mDrawableTheme
                binding.themeP
            }
            16 -> {
                binding.themeQ.foreground = mDrawableTheme
                binding.themeQ
            }
            17 -> {
                binding.themeR.foreground = mDrawableTheme
                binding.themeR
            }
            18 -> {
                binding.themeS.foreground = mDrawableTheme
                binding.themeS
            }
            19 -> {
                binding.themeT.foreground = mDrawableTheme
                binding.themeT
            }
            20 -> {
                binding.themeU.foreground = mDrawableTheme
                binding.themeU
            }
            21 -> {
                binding.themeG1.foreground = mDrawableTheme
                binding.themeG1
            }
            22 -> {
                binding.themeG2.foreground = mDrawableTheme
                binding.themeG2
            }
            23 -> {
                binding.themeG3.foreground = mDrawableTheme
                binding.themeG3
            }
            24 -> {
                binding.themeG4.foreground = mDrawableTheme
                binding.themeG4
            }
            25 -> {
                binding.themeG5.foreground = mDrawableTheme
                binding.themeG5
            }
            26 -> {
                binding.themeG6.foreground = mDrawableTheme
                binding.themeG6
            }
            27 -> {
                binding.themeG7.foreground = mDrawableTheme
                binding.themeG7
            }
            28 -> {
                binding.themeG8.foreground = mDrawableTheme
                binding.themeG8
            }
            29 -> {
                binding.themeG9.foreground = mDrawableTheme
                binding.themeG9
            }
            30 -> {
                binding.themeG10.foreground = mDrawableTheme
                binding.themeG10
            }
            31 -> {
                binding.themeG11.foreground = mDrawableTheme
                binding.themeG11
            }
            32 -> {
                binding.themeG12.foreground = mDrawableTheme
                binding.themeG12
            }
            33 -> {
                binding.themeG13.foreground = mDrawableTheme
                binding.themeG13
            }
            34 -> {
                binding.themeG14.foreground = mDrawableTheme
                binding.themeG14
            }
            35 -> {
                binding.themeG15.foreground = mDrawableTheme
                binding.themeG15
            }
            36 -> {
                binding.themeG16.foreground = mDrawableTheme
                binding.themeG16
            }
            37 -> {
                binding.themeG17.foreground = mDrawableTheme
                binding.themeG17
            }
            38 -> {
                binding.themeG18.foreground = mDrawableTheme
                binding.themeG18
            }
            39 -> {
                binding.themeG19.foreground = mDrawableTheme
                binding.themeG19
            }
            40 -> {
                binding.themeG20.foreground = mDrawableTheme
                binding.themeG20
            }
            else -> {
                binding.themeG21.foreground = mDrawableTheme
                binding.themeG21
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (isKeyboardOpen) {
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }
}