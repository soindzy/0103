package com.example.zapisnaya_knizhka

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val notes = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<EditText>(R.id.editText)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val listView = findViewById<ListView>(R.id.listView)

        // Устанавливаем фокус на поле ввода
        editText.requestFocus()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notes)
        listView.adapter = adapter

        saveButton.setOnClickListener {
            val note = editText.text.toString()
            if (note.isNotEmpty()) {
                notes.add(note)
                adapter.notifyDataSetChanged()
                editText.text.clear()
                editText.requestFocus() // Возвращаем фокус после сохранения
                Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Введите текст", Toast.LENGTH_SHORT).show()
            }
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            notes.removeAt(position)
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Удалено", Toast.LENGTH_SHORT).show()
        }
    }
}