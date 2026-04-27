package com.example.uroven_i_uglomer

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.*

class MainActivity : Activity(), SensorEventListener {
    private lateinit var sm: SensorManager
    private lateinit var view: LevelView
    private lateinit var text: TextView

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.parseColor("#1a1a2e")) }
        view = LevelView(this).apply { layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f) }
        text = TextView(this).apply { textSize = 32f; setTextColor(Color.WHITE); gravity = android.view.Gravity.CENTER; setTypeface(null, Typeface.BOLD) }
        text.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 50 }
        layout.addView(view); layout.addView(text); setContentView(layout)
    }

    override fun onResume() { super.onResume(); sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let { sm.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) } }
    override fun onPause() { super.onPause(); sm.unregisterListener(this) }
    override fun onAccuracyChanged(s: Sensor?, a: Int) {}

    override fun onSensorChanged(e: SensorEvent?) {
        if(e?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = e.values[0]; val y = e.values[1]; val z = e.values[2]
            val pitch = atan2(-x, sqrt(y*y + z*z)) * 180 / PI.toFloat()
            val roll = atan2(y, z) * 180 / PI.toFloat()
            view.update(pitch, roll)
            text.text = String.format("%.1f° | %.1f°", pitch, roll)
        }
    }
}

class LevelView(ctx: Context) : android.view.View(ctx) {
    private var x=0f; private var y=0f; private val p=Paint(Paint.ANTI_ALIAS_FLAG)
    fun update(pitch:Float, roll:Float) { x=pitch; y=roll; invalidate() }

    override fun onDraw(c:Canvas) {
        val w=width.toFloat(); val h=height.toFloat(); val cx=w/2; val cy=h/2; val r=min(w,h)*0.35f

        // Круг и сетка
        p.apply { color=Color.WHITE; style=Paint.Style.STROKE; strokeWidth=4f }; c.drawCircle(cx,cy,r,p)
        p.strokeWidth=2f; c.drawLine(cx-r,cy,cx+r,cy,p); c.drawLine(cx,cy-r,cx,cy+r,p)

        // Деления
        p.color=Color.parseColor("#888888")
        for(i in -30..30 step 10) {
            val rad = i.toFloat() * PI.toFloat() / 180f
            val x1 = cx + (r-15) * sin(rad).toFloat()
            val y1 = cy - (r-15) * cos(rad).toFloat()
            val x2 = cx + (r-5) * sin(rad).toFloat()
            val y2 = cy - (r-5) * cos(rad).toFloat()
            c.drawLine(x1,y1,x2,y2,p)
        }

        // Пузырек
        val maxOff = r * 0.6f
        val offX = (y/45f).coerceIn(-1f,1f) * maxOff
        val offY = (x/45f).coerceIn(-1f,1f) * maxOff

        p.style=Paint.Style.FILL
        p.color = if(abs(x)<2 && abs(y)<2) Color.GREEN else Color.parseColor("#FFD700")
        c.drawCircle(cx+offX, cy+offY, 25f, p)
        p.color=Color.BLACK; p.strokeWidth=2f; p.style=Paint.Style.STROKE
        c.drawCircle(cx+offX, cy+offY, 25f, p)

        // Центр
        p.style=Paint.Style.FILL; p.color=Color.WHITE
        c.drawCircle(cx,cy,8f,p)

        // Текст
        p.color=Color.WHITE; p.textSize=28f; p.textAlign=Paint.Align.CENTER; p.style=Paint.Style.FILL
        c.drawText("УРОВЕНЬ", cx, cy-r-20, p)
    }
}