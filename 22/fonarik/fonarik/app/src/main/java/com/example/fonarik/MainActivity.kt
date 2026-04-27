package com.example.fonarik

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : Activity() {
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private var isFlashOn = false
    private lateinit var toggleBtn: Button
    private lateinit var statusText: TextView

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1a1a2e"))
            gravity = android.view.Gravity.CENTER
        }

        val title = TextView(this).apply {
            text = "🔦 Фонарик"
            textSize = 32f
            setTextColor(Color.WHITE)
            gravity = android.view.Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 80 }
        }

        statusText = TextView(this).apply {
            text = "Выключен"
            textSize = 20f
            setTextColor(Color.parseColor("#888888"))
            gravity = android.view.Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 50 }
        }

        toggleBtn = Button(this).apply {
            text = "ВКЛ"
            textSize = 32f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#4CAF50"))
            layoutParams = LinearLayout.LayoutParams(
                250,
                250
            )
            setOnClickListener { toggleFlashlight() }
        }

        layout.addView(title)
        layout.addView(statusText)
        layout.addView(toggleBtn)
        setContentView(layout)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        if (!hasFlash()) {
            statusText.text = "Фонарик не поддерживается"
            toggleBtn.isEnabled = false
            return
        }

        cameraId = cameraManager.cameraIdList[0]

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun hasFlash(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    private fun toggleFlashlight() {
        try {
            if (isFlashOn) {
                cameraManager.setTorchMode(cameraId, false)
                isFlashOn = false
                toggleBtn.text = "ВКЛ"
                toggleBtn.setBackgroundColor(Color.parseColor("#4CAF50"))
                statusText.text = "Выключен"
                statusText.setTextColor(Color.parseColor("#888888"))
            } else {
                cameraManager.setTorchMode(cameraId, true)
                isFlashOn = true
                toggleBtn.text = "ВЫКЛ"
                toggleBtn.setBackgroundColor(Color.parseColor("#F44336"))
                statusText.text = "Включен"
                statusText.setTextColor(Color.parseColor("#FFD700"))
            }
        } catch(e: Exception) {
            statusText.text = "Ошибка: ${e.message}"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                statusText.text = "Разрешение получено"
            } else {
                statusText.text = "Нужно разрешение для камеры"
                toggleBtn.isEnabled = false
            }
        }
    }
}