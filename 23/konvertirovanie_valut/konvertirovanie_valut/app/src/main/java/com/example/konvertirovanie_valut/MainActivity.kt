package com.example.konvertirovanie_valut

import android.app.Activity
import android.os.Bundle
import android.widget.*
import android.graphics.Color

class MainActivity : Activity() {
    private lateinit var amountInput: EditText
    private lateinit var fromSpinner: Spinner
    private lateinit var toSpinner: Spinner
    private lateinit var resultText: TextView
    private lateinit var convertBtn: Button

    // Курсы валют относительно RUB (1 единица валюты = X RUB)
    private val rates = mapOf(
        "RUB" to 1.0,
        "USD" to 97.5,
        "EUR" to 105.2,
        "GBP" to 126.8,
        "CNY" to 13.4,
        "TRY" to 2.8
    )

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1a1a2e"))
            setPadding(50, 100, 50, 50)
        }

        val title = TextView(this).apply {
            text = "💰 Конвертер валют"
            textSize = 28f
            setTextColor(Color.WHITE)
            gravity = android.view.Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 50 }
        }

        amountInput = EditText(this).apply {
            hint = "Введите сумму"
            setHintTextColor(Color.parseColor("#888888"))
            textSize = 24f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#2a2a3e"))
            setPadding(30, 25, 30, 25)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 30 }
        }

        val fromLabel = TextView(this).apply {
            text = "Из валюты:"
            textSize = 16f
            setTextColor(Color.parseColor("#aaaaaa"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 10 }
        }

        fromSpinner = createSpinner()

        val toLabel = TextView(this).apply {
            text = "В валюту:"
            textSize = 16f
            setTextColor(Color.parseColor("#aaaaaa"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 20
                bottomMargin = 10
            }
        }

        toSpinner = createSpinner()

        convertBtn = Button(this).apply {
            text = "Конвертировать"
            textSize = 22f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#4CAF50"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 30 }
            setOnClickListener { convert() }
        }

        resultText = TextView(this).apply {
            text = "Результат: 0.00"
            textSize = 28f
            setTextColor(Color.parseColor("#FFD700"))
            gravity = android.view.Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 40 }
        }

        layout.addView(title)
        layout.addView(amountInput)
        layout.addView(fromLabel)
        layout.addView(fromSpinner)
        layout.addView(toLabel)
        layout.addView(toSpinner)
        layout.addView(convertBtn)
        layout.addView(resultText)
        setContentView(layout)
    }

    private fun createSpinner(): Spinner {
        val currencies = rates.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        return Spinner(this).apply {
            this.adapter = adapter
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(Color.parseColor("#2a2a3e"))
        }
    }

    private fun convert() {
        try {
            val amount = amountInput.text.toString().toDoubleOrNull() ?: 0.0
            val from = fromSpinner.selectedItem.toString()
            val to = toSpinner.selectedItem.toString()

            val rubAmount = amount * (rates[from] ?: 1.0)
            val result = rubAmount / (rates[to] ?: 1.0)

            resultText.text = String.format("%.2f %s = %.2f %s", amount, from, result, to)
        } catch(e: Exception) {
            resultText.text = "Ошибка: введите число"
        }
    }
}