package com.example.pesochnie_chasi

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.min

class MainActivity : Activity() {
    private lateinit var sand: SandView
    private var total = 60000L
    private var remain = 60000L
    private var running = false
    private var flipping = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeText: TextView
    private lateinit var flipText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1a1a2e"))
        }

        sand = SandView(this)
        sand.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1f
        )
        sand.setOnTouchListener { _, _ ->
            flip()
            true
        }

        timeText = TextView(this).apply {
            text = "01:00"
            textSize = 48f
            setTextColor(Color.WHITE)
            gravity = android.view.Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
        }
        timeText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = 20 }

        flipText = TextView(this).apply {
            text = "👇 Нажмите на часы, чтобы перевернуть 👇"
            textSize = 16f
            setTextColor(Color.parseColor("#aaaaaa"))
            gravity = android.view.Gravity.CENTER
        }
        flipText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 30
            topMargin = 20
        }

        layout.addView(sand)
        layout.addView(timeText)
        layout.addView(flipText)
        setContentView(layout)

        startTimer()
    }

    private fun startTimer() {
        if (running) return
        running = true
        val startTime = System.currentTimeMillis()
        val startRemain = remain

        handler.post(object : Runnable {
            override fun run() {
                if (!running) return
                remain = (startRemain - (System.currentTimeMillis() - startTime)).coerceAtLeast(0)
                val sec = (remain / 1000).toInt()
                timeText.text = String.format("%02d:%02d", sec / 60, sec % 60)
                sand.updateSandLevel(remain.toFloat() / total)

                if (remain > 0) {
                    handler.postDelayed(this, 50)
                } else {
                    running = false
                    sand.updateSandLevel(0f)
                    flipText.text = "⏰ Время вышло! Нажмите, чтобы перевернуть ⏰"
                }
            }
        })
    }

    private fun flip() {
        if (flipping) return
        flipping = true
        running = false
        sand.animateFlip()

        handler.postDelayed({
            remain = total - remain
            flipping = false
            if (remain > 0) {
                startTimer()
                flipText.text = "⏳ Песок сыплется... Нажмите, чтобы перевернуть ⏳"
            } else {
                flipText.text = "👇 Нажмите на часы, чтобы перевернуть 👇"
            }
        }, 300)
    }
}

class SandView(context: Context) : View(context) {
    private var level = 1f
    private var animating = false
    private var animProgress = 0f
    private val sandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#F4C430") }
    private val glassPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#88ffffff")
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    fun updateSandLevel(l: Float) {
        if (!animating) {
            level = l.coerceIn(0f, 1f)
            invalidate()
        }
    }

    fun animateFlip() {
        animating = true
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 300
            addUpdateListener {
                animProgress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            animating = false
            animProgress = 0f
            level = 1f - level
            invalidate()
        }, 300)
    }

    override fun onDraw(c: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        val cx = w / 2
        val top = h * 0.15f
        val bottom = h * 0.85f
        val neck = h / 2
        val neckW = w * 0.12f

        c.save()
        if (animating) {
            c.scale(1f, 1f - 2f * min(animProgress, 0.5f), cx, h / 2)
        }

        val topPath = Path().apply {
            moveTo(cx - neckW / 2, neck)
            lineTo(cx - w * 0.3f, top)
            lineTo(cx + w * 0.3f, top)
            lineTo(cx + neckW / 2, neck)
            close()
        }

        val bottomPath = Path().apply {
            moveTo(cx - neckW / 2, neck)
            lineTo(cx - w * 0.3f, bottom)
            lineTo(cx + w * 0.3f, bottom)
            lineTo(cx + neckW / 2, neck)
            close()
        }

        // Рисуем стекло
        glassPaint.style = Paint.Style.FILL
        glassPaint.color = Color.parseColor("#20ffffff")
        c.drawPath(topPath, glassPaint)
        c.drawPath(bottomPath, glassPaint)

        // Песок верх
        if (level > 0) {
            c.save()
            c.clipPath(topPath)
            c.drawRect(
                cx - w * 0.3f,
                top + (neck - top) * (1f - level),
                cx + w * 0.3f,
                neck,
                sandPaint
            )
            c.restore()
        }

        // Песок низ
        if (level < 1) {
            c.save()
            c.clipPath(bottomPath)
            c.drawRect(
                cx - w * 0.3f,
                neck,
                cx + w * 0.3f,
                bottom - (bottom - neck) * level,
                sandPaint
            )
            c.restore()
        }

        // Падающие песчинки
        if (!animating && level in 0.01f..0.99f) {
            sandPaint.color = Color.parseColor("#D4A020")
            for (i in -2..2) {
                c.drawCircle(
                    cx + i * 4f,
                    neck + (1f - level) * 80f + i * 5f,
                    3f,
                    sandPaint
                )
            }
            sandPaint.color = Color.parseColor("#F4C430")
        }

        // Контуры
        glassPaint.style = Paint.Style.STROKE
        glassPaint.color = Color.parseColor("#CCffffff")
        c.drawPath(topPath, glassPaint)
        c.drawPath(bottomPath, glassPaint)

        glassPaint.strokeWidth = 6f
        c.drawLine(cx - neckW / 2, neck, cx + neckW / 2, neck, glassPaint)

        glassPaint.strokeWidth = 5f
        glassPaint.color = Color.parseColor("#FFD700")
        c.drawLine(cx - w * 0.32f, top, cx + w * 0.32f, top, glassPaint)
        c.drawLine(cx - w * 0.32f, bottom, cx + w * 0.32f, bottom, glassPaint)

        c.restore()
    }
}