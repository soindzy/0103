package com.example.taimer_


import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.SeekBar
import android.graphics.Color

class MainActivity : Activity() {
    private lateinit var timeText: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var startBtn: Button
    private lateinit var statusText: TextView

    private var timer: CountDownTimer? = null
    private var remaining = 60000L // 60 секунд
    private var total = 60000L
    private var running = false

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1a1a2e"))
        }

        timeText = TextView(this).apply {
            text = "01:00"
            textSize = 72f
            setTextColor(Color.WHITE)
            gravity = android.view.Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 100 }
        }

        seekBar = SeekBar(this).apply {
            max = 300 // 5 минут
            progress = 60
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin = 50
                rightMargin = 50
                topMargin = 50
            }
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar?, p: Int, from: Boolean) {
                    if(!running && from) {
                        total = p * 1000L
                        remaining = total
                        updateDisplay()
                    }
                }
                override fun onStartTrackingTouch(s: SeekBar?) {}
                override fun onStopTrackingTouch(s: SeekBar?) {}
            })
        }

        startBtn = Button(this).apply {
            text = "Старт"
            textSize = 24f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#4CAF50"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 50
                gravity = android.view.Gravity.CENTER
            }
            setOnClickListener {
                if(running) stop() else start()
            }
        }

        statusText = TextView(this).apply {
            text = "⚡ Готов ⚡"
            textSize = 18f
            setTextColor(Color.parseColor("#aaaaaa"))
            gravity = android.view.Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 30 }
        }

        layout.addView(timeText)
        layout.addView(seekBar)
        layout.addView(startBtn)
        layout.addView(statusText)
        setContentView(layout)
    }

    private fun start() {
        if(remaining <= 0) return
        running = true
        startBtn.text = "Стоп"
        startBtn.setBackgroundColor(Color.parseColor("#F44336"))
        seekBar.isEnabled = false
        statusText.text = "⏳ Таймер запущен ⏳"

        timer = object : CountDownTimer(remaining, 1000) {
            override fun onTick(millis: Long) {
                remaining = millis
                updateDisplay()
            }
            override fun onFinish() {
                reset()
                statusText.text = "✅ Время вышло! ✅"
            }
        }.start()
    }

    private fun stop() {
        timer?.cancel()
        running = false
        startBtn.text = "Старт"
        startBtn.setBackgroundColor(Color.parseColor("#4CAF50"))
        seekBar.isEnabled = true
        statusText.text = "⏸ Пауза ⏸"
    }

    private fun reset() {
        timer?.cancel()
        running = false
        remaining = total
        updateDisplay()
        startBtn.text = "Старт"
        startBtn.setBackgroundColor(Color.parseColor("#4CAF50"))
        seekBar.isEnabled = true
        statusText.text = "🔄 Перезапустите таймер 🔄"
    }

    private fun updateDisplay() {
        val sec = (remaining / 1000).toInt()
        val min = sec / 60
        val s = sec % 60
        timeText.text = String.format("%02d:%02d", min, s)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
