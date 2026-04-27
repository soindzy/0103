package com.example.shagomer

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.graphics.Color
import kotlin.math.sqrt

class MainActivity : Activity(), SensorEventListener {
    private lateinit var sm: SensorManager
    private var steps = 0
    private var stepLength = 0.73f // метры
    private var distance = 0f

    // Для обнаружения шагов
    private var lastAccel = 0f
    private var lastTime = 0L
    private val threshold = 12f // порог чувствительности
    private val minInterval = 300L // мин интервал между шагами (мс)

    private lateinit var stepsText: TextView
    private lateinit var distText: TextView
    private lateinit var resetBtn: Button
    private lateinit var statusText: TextView

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)

        sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1a1a2e"))
        }

        stepsText = TextView(this).apply {
            text = "0"
            textSize = 80f
            setTextColor(Color.WHITE)
            gravity = android.view.Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 100 }
        }

        val stepsLabel = TextView(this).apply {
            text = "шагов"
            textSize = 24f
            setTextColor(Color.parseColor("#aaaaaa"))
            gravity = android.view.Gravity.CENTER
        }

        distText = TextView(this).apply {
            text = "0 м"
            textSize = 36f
            setTextColor(Color.parseColor("#87CEEB"))
            gravity = android.view.Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 40 }
        }

        statusText = TextView(this).apply {
            text = "🏃 Идите, чтобы начать 🏃"
            textSize = 18f
            setTextColor(Color.parseColor("#888888"))
            gravity = android.view.Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 30 }
        }

        resetBtn = Button(this).apply {
            text = "Сброс"
            textSize = 22f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#FF6B6B"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 60
                gravity = android.view.Gravity.CENTER
            }
            setOnClickListener { reset() }
        }

        layout.addView(stepsText)
        layout.addView(stepsLabel)
        layout.addView(distText)
        layout.addView(statusText)
        layout.addView(resetBtn)
        setContentView(layout)
    }

    override fun onResume() {
        super.onResume()
        sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            sm.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sm.unregisterListener(this)
    }

    override fun onSensorChanged(e: SensorEvent?) {
        if(e?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = e.values[0]
            val y = e.values[1]
            val z = e.values[2]

            // Вычисляем ускорение
            val accel = sqrt(x*x + y*y + z*z)
            val now = System.currentTimeMillis()

            // Обнаружение шага
            if(accel > threshold && (now - lastTime) > minInterval) {
                if(lastAccel > 0 && accel > lastAccel * 1.5f) {
                    steps++
                    distance = steps * stepLength
                    updateUI()
                    statusText.text = "🚶 Шаг засчитан! 🚶"
                    lastTime = now

                    // Анимация
                    stepsText.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction {
                        stepsText.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    }.start()
                }
            }

            lastAccel = accel
        }
    }

    private fun updateUI() {
        stepsText.text = steps.toString()
        val km = distance / 1000
        if(km >= 1) {
            distText.text = String.format("%.2f км", km)
        } else {
            distText.text = String.format("%.0f м", distance)
        }
    }

    private fun reset() {
        steps = 0
        distance = 0f
        lastAccel = 0f
        lastTime = 0L
        updateUI()
        statusText.text = "🔄 Сброшено! Идите снова 🔄"
    }

    override fun onAccuracyChanged(s: Sensor?, a: Int) {}
}