package com.example.koreankeyboard

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.koreankeyboard.R

class MicPermissionActivity : AppCompatActivity() {
    private val micPermissionRequest = 1032
    val requestCode = 99
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mic_permission)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                micPermissionRequest
            )
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == micPermissionRequest && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(
                this,
                "Thanks for giving Mic permission, Now enjoy speech inout.",
                Toast.LENGTH_SHORT
            ).show()
            onBackPressed()
        } else {
            AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle("Mic Permission")
                .setMessage("Go to Permissions")
                .setPositiveButton(R.string.ok) { _, i ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivityForResult(intent, requestCode)
                }
                .setOnDismissListener {
                    Toast.makeText(
                        this,
                        "Mic permission is required for speech input.",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    onBackPressed()
                }
                .create()
                .show()

//            onBackPressed()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == this.requestCode) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                onBackPressed()
            } else {
                Toast.makeText(
                    this,
                    "Mic permission is required for speech input.",
                    Toast.LENGTH_LONG
                ).show()
                onBackPressed()
            }
        }
    }
}