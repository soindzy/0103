package com.example.clock

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.parseColor("#1a1a2e")) }
        val clock = AnalogClockView(this)
        clock.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        val digital = TextView(this).apply { textSize = 48f; setTextColor(Color.WHITE); gravity = android.view.Gravity.CENTER; setTypeface(null, Typeface.BOLD) }
        digital.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 50 }
        layout.addView(clock)
        layout.addView(digital)
        setContentView(layout)

        Handler(Looper.getMainLooper()).post(object : Runnable {
            override fun run() {
                val c = Calendar.getInstance()
                digital.text = String.format("%02d:%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND))
                clock.update(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND))
                Handler(Looper.getMainLooper()).postDelayed(this, 1000)
            }
        })
    }
}

class AnalogClockView(context: Context) : android.view.View(context) {
    private var h=0; private var m=0; private var s=0; private val p=Paint(Paint.ANTI_ALIAS_FLAG)
    fun update(hour:Int, min:Int, sec:Int) { h=hour; m=min; s=sec; invalidate() }
    override fun onDraw(c:Canvas) {
        val x=width/2f; val y=height/2f; val r = Math.min(width,height)*0.4f
        p.apply { color=Color.WHITE; style=Paint.Style.STROKE; strokeWidth=5f }
        c.drawCircle(x,y,r,p)
        p.strokeWidth=8f
        for(i in 0..11){ val a=Math.toRadians((i*30-90).toDouble()); val cs=cos(a).toFloat(); val sn=sin(a).toFloat()
            c.drawLine(x+(r-20)*cs, y+(r-20)*sn, x+r*cs, y+r*sn, p) }
        val ha = Math.toRadians(((h%12)*30 + m*0.5 - 90).toDouble())
        p.color=Color.parseColor("#FFD700"); p.strokeWidth=12f
        c.drawLine(x,y,x+r*0.5f*cos(ha).toFloat(),y+r*0.5f*sin(ha).toFloat(), p)
        val ma = Math.toRadians((m*6 + s*0.1 - 90).toDouble())
        p.color=Color.parseColor("#87CEEB"); p.strokeWidth=8f
        c.drawLine(x,y,x+r*0.7f*cos(ma).toFloat(),y+r*0.7f*sin(ma).toFloat(), p)
        val sa = Math.toRadians((s*6 - 90).toDouble())
        p.color=Color.RED; p.strokeWidth=4f
        c.drawLine(x,y,x+r*0.8f*cos(sa).toFloat(),y+r*0.8f*sin(sa).toFloat(), p)
        p.style=Paint.Style.FILL; p.color=Color.WHITE
        c.drawCircle(x,y,8f,p)
    }
}