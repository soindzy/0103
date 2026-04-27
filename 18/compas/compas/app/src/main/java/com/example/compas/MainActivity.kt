package com.example.compas

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
    private lateinit var cv: CompassView
    private lateinit var dt: TextView
    private var az = 0f

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val l = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.parseColor("#1a1a2e")) }
        cv = CompassView(this).apply { layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f) }
        dt = TextView(this).apply { textSize = 32f; setTextColor(Color.WHITE); gravity = android.view.Gravity.CENTER; setTypeface(null, Typeface.BOLD) }
        dt.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 50 }
        l.addView(cv); l.addView(dt); setContentView(l)
    }

    override fun onResume() { super.onResume(); sm.getDefaultSensor(Sensor.TYPE_ORIENTATION)?.let { sm.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) } }
    override fun onPause() { super.onPause(); sm.unregisterListener(this) }
    override fun onAccuracyChanged(s: Sensor?, a: Int) {}

    override fun onSensorChanged(e: SensorEvent?) {
        if(e?.sensor?.type == Sensor.TYPE_ORIENTATION) {
            az = e.values[0]
            cv.updateAzimuth(az)
            val d = az.roundToInt()
            val dir = when(d) { in 0..22->"С"; in 23..67->"СВ"; in 68..112->"В"; in 113..157->"ЮВ"; in 158..202->"Ю"; in 203..247->"ЮЗ"; in 248..292->"З"; in 293..337->"СЗ"; else->"С" }
            dt.text = "$d° $dir"
        }
    }
}

class CompassView(ctx: Context) : android.view.View(ctx) {
    private var az=0f; private val p=Paint(Paint.ANTI_ALIAS_FLAG)
    fun updateAzimuth(a:Float) { az=a; invalidate() }
    override fun onDraw(c:Canvas) {
        val x=width/2f; val y=height/2f; val r=min(width,height)*0.4f
        c.save(); c.rotate(-az,x,y)
        p.apply { color=Color.WHITE; style=Paint.Style.STROKE; strokeWidth=4f }; c.drawCircle(x,y,r,p)
        p.textSize=40f; p.textAlign=Paint.Align.CENTER; p.style=Paint.Style.FILL
        p.color=Color.RED; c.drawText("N",x,y-r+40,p); p.color=Color.WHITE
        c.drawText("S",x,y+r-20,p); c.drawText("W",x-r+40,y+15,p); c.drawText("E",x+r-40,y+15,p)
        p.strokeWidth=3f
        for(i in 0..71){ val a=Math.toRadians((i*5).toDouble()); val cs=cos(a).toFloat(); val sn=sin(a).toFloat(); val l=if(i%18==0)25f else 15f
            c.drawLine(x+(r-l)*cs,y+(r-l)*sn,x+r*cs,y+r*sn,p) }
        p.color=Color.RED; p.style=Paint.Style.FILL
        Path().apply { moveTo(x,y-r+15); lineTo(x-15,y-r+45); lineTo(x-5,y-r+45); lineTo(x-5,y+r-30); lineTo(x+5,y+r-30); lineTo(x+5,y-r+45); lineTo(x+15,y-r+45); close() }.let { c.drawPath(it,p) }
        p.color=Color.WHITE; c.drawCircle(x,y,10f,p); p.color=Color.RED; c.drawCircle(x,y,5f,p)
        c.restore()
    }
}