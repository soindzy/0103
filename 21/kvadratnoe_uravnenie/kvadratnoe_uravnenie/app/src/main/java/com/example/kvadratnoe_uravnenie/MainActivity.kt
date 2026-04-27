package com.example.kvadratnoe_uravnenie

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import kotlin.math.sqrt

class MainActivity : Activity() {
    private lateinit var aInput: EditText
    private lateinit var bInput: EditText
    private lateinit var cInput: EditText
    private lateinit var resultText: TextView
    private lateinit var solveBtn: Button

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1a1a2e"))
            setPadding(50, 100, 50, 50)
        }

        val title = TextView(this).apply {
            text = "Квадратное уравнение"
            textSize = 28f
            setTextColor(Color.WHITE)
            gravity = android.view.Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 50 }
        }

        val formula = TextView(this).apply {
            text = "ax² + bx + c = 0"
            textSize = 24f
            setTextColor(Color.parseColor("#87CEEB"))
            gravity = android.view.Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 50 }
        }

        aInput = createInput("Введите a")
        bInput = createInput("Введите b")
        cInput = createInput("Введите c")

        solveBtn = Button(this).apply {
            text = "Решить"
            textSize = 24f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#4CAF50"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 30 }
            setOnClickListener { solve() }
        }

        resultText = TextView(this).apply {
            text = "Введите коэффициенты"
            textSize = 18f
            setTextColor(Color.parseColor("#aaaaaa"))
            gravity = android.view.Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 40 }
        }

        layout.addView(title)
        layout.addView(formula)
        layout.addView(aInput)
        layout.addView(bInput)
        layout.addView(cInput)
        layout.addView(solveBtn)
        layout.addView(resultText)
        setContentView(layout)
    }

    private fun createInput(hint: String): EditText {
        return EditText(this).apply {
            this.hint = hint
            setHintTextColor(Color.parseColor("#888888"))
            textSize = 20f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#2a2a3e"))
            setPadding(20, 20, 20, 20)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_SIGNED or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 20
                topMargin = 10
            }
        }
    }

    private fun solve() {
        try {
            val a = aInput.text.toString().toDoubleOrNull() ?: 0.0
            val b = bInput.text.toString().toDoubleOrNull() ?: 0.0
            val c = cInput.text.toString().toDoubleOrNull() ?: 0.0

            if(a == 0.0) {
                resultText.text = "Ошибка: a не может быть 0"
                resultText.setTextColor(Color.parseColor("#FF6B6B"))
                return
            }

            val d = b * b - 4 * a * c

            resultText.setTextColor(Color.parseColor("#87CEEB"))

            when {
                d < 0 -> {
                    resultText.text = "Нет действительных корней\nD = ${String.format("%.2f", d)}"
                }
                d == 0.0 -> {
                    val x = -b / (2 * a)
                    resultText.text = "Один корень:\nx = ${String.format("%.3f", x)}\nD = 0"
                }
                else -> {
                    val sqrtD = sqrt(d)
                    val x1 = (-b + sqrtD) / (2 * a)
                    val x2 = (-b - sqrtD) / (2 * a)
                    resultText.text = "x₁ = ${String.format("%.3f", x1)}\nx₂ = ${String.format("%.3f", x2)}\nD = ${String.format("%.2f", d)}"
                }
            }
        } catch(e: Exception) {
            resultText.text = "Ошибка: введите числа"
            resultText.setTextColor(Color.parseColor("#FF6B6B"))
        }
    }
}