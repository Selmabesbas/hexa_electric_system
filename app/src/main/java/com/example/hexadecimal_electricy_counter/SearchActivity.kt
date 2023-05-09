package com.example.hexadecimal_electricy_counter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

class SearchActivity : AppCompatActivity() {

    private lateinit var editTextSearch: EditText
    private lateinit var buttonSearch: Button
    private lateinit var listViewResults: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        editTextSearch = findViewById(R.id.editTextSearch)
        buttonSearch = findViewById(R.id.buttonSearch)
        listViewResults = findViewById(R.id.listViewResults)

        buttonSearch.setOnClickListener {
            val query = editTextSearch.text.toString()
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        // Simulating the retrieval of hex values based on the search query
        val retrievedHexValues = retrieveHexValues(query)

        // Create an ArrayAdapter to display the retrieved hex values in the ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, retrievedHexValues)
        listViewResults.adapter = adapter
    }

    private fun retrieveHexValues(query: String): List<String> {
        val filename = "hex_values.txt"
        val retrievedHexValues = mutableListOf<String>()

        try {
            val file = File(filesDir, filename)
            val fileReader = FileReader(file)
            val bufferedReader = BufferedReader(fileReader)

            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                // Check if the line contains the search query
                if (line?.contains(query, ignoreCase = true) == true) {
                    retrievedHexValues.add(line!!)
                }
            }

            bufferedReader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return retrievedHexValues
    }
}