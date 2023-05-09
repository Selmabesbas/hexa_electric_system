package com.example.hexadecimal_electricy_counter

// MainActivity.kt

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.*

class MainActivity : AppCompatActivity() {
    private val EDIT_REQUEST_CODE = 1

    private lateinit var hexInput: EditText
    private lateinit var addButton: Button
    private lateinit var searchButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hexInput = findViewById(R.id.editText_hex_input)
        addButton = findViewById(R.id.button_add)
        searchButton = findViewById(R.id.button_search)
        recyclerView = findViewById(R.id.recyclerView_results)

        searchButton.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            result.data?.extras?.let {
                val resultItem: ResultItem = it.getParcelable<ResultItem>(EditHex.EXTRA_ITEM)!!
                adapter.updateItem(it.getInt(EditHex.EXTRA_POSITION),resultItem)

                it.getString("old_value")
                    ?.let { it1 -> updateHexValueInFile(it1,resultItem.hexValue) }
            };
            adapter.notifyDataSetChanged()
        }

        val itemClickListener = object : ResultAdapter.ItemClickListener {
            override fun onEditItemClick(item: ResultItem,position: Int) {

                val intent = Intent(applicationContext, EditHex::class.java)
                intent.putExtra(EditHex.EXTRA_ITEM, item)
                intent.putExtra(EditHex.EXTRA_POSITION,position)
                resultLauncher.launch(intent)


            }

            override fun onDeleteItemClick(item: ResultItem,position: Int) {
                adapter.removeItem(position)
                removeHexValueFromFile(item.hexValue)
            }
        }
        val hexValues: MutableList<ResultItem> = retrieveHexValues();
        adapter = ResultAdapter(itemClickListener,hexValues)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        addButton.setOnClickListener { addHexValue() }
    }

    private fun updateHexValueInFile(oldHexValue: String, newHexValue: String) {
        val filename = "hex_values.txt"
        val updatedLines = mutableListOf<String>()

        try {
            val file = File(filesDir, filename)
            val fileReader = FileReader(file)
            val bufferedReader = BufferedReader(fileReader)

            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                if (line == oldHexValue) {
                    updatedLines.add(newHexValue)
                } else {
                    updatedLines.add(line!!)
                }
            }

            bufferedReader.close()

            // Write the updated lines back to the file
            val fileWriter = FileWriter(file, false) // Open the file in write mode
            val bufferedWriter = BufferedWriter(fileWriter)
            for (updatedLine in updatedLines) {
                bufferedWriter.write(updatedLine)
                bufferedWriter.newLine()
            }
            bufferedWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun addHexValue() {
        val hexValue = hexInput.text.toString()
        val binaryValue = hexToBinary(hexValue)
        val message = generateMessage(binaryValue)

        val newItem = ResultItem(hexValue, binaryValue, message)
        adapter.addItem(newItem)
        recyclerView.scrollToPosition(adapter.itemCount - 1)
        saveHexToFile(hexValue)
    }

    private fun hexToBinary(hex: String): String {
        val decimal = hex.toLong(16)
        val binary = java.lang.Long.toBinaryString(decimal)
        return binary.padStart(32, '0')
    }

    private fun removeHexValueFromFile(hexValue: String) {
        val filename = "hex_values.txt"
        val updatedLines = mutableListOf<String>()

        try {
            val file = File(filesDir, filename)
            val fileReader = FileReader(file)
            val bufferedReader = BufferedReader(fileReader)

            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                if (line != hexValue) {
                    updatedLines.add(line!!)
                }
            }

            bufferedReader.close()

            // Write the updated lines back to the file
            val fileWriter = FileWriter(file, false) // Open the file in write mode
            val bufferedWriter = BufferedWriter(fileWriter)
            for (updatedLine in updatedLines) {
                bufferedWriter.write(updatedLine)
                bufferedWriter.newLine()
            }
            bufferedWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun generateMessage(binary: String): String {
        val messages = arrayOf(
            "Déclenchement du chien de garde",
            "Alarme Pile",
            "Perte de neutre",
            "Température",

            "Erreur de communications",
            "Incohérence de programmation",
            "Perte de l'heure",
            "Incohérence de l'horloge externe",

            "Perte de tension (Phase1)",
            "Creux de tension (Phase1)",
            "Surtension (Phase1)",
            "Inversion de courant (Phase1)",

            "Perte de tension (Phase2)",
            "Creux de tension (Phase2)",
            "Surtension (Phase2)",
            "Inversion de courant (Phase2)",

            "Perte de tension (Phase3)",
            "Creux de tension (Phase3)",
            "Surtension (Phase3)",
            "Inversion de courant (Phase3)",

            "Absence de consomation interne",
            "Alarme entée1,Alarme entée2,Alarme entée3",
            "Tension Résiduelle",
            "Intensité neutre",

            "Non utilisé",
            "Incohérence de la Configuration",
            "Mémoire non volatile",
            "Puissance Maximale",

            "Non utilisé",
            "Non utilisé",
            "Non utilisé",
            "Ouverture de couvre borne",




        )

        val stringBuilder = StringBuilder()
        for ((index, bit) in binary.reversed().withIndex()) {
            if (bit == '1') {
                val message = messages[index]
                stringBuilder.append("$message\n")
            }
        }
        return stringBuilder.toString()
    }

    private fun retrieveHexValues(): MutableList<ResultItem> {
        val filename = "hex_values.txt"
        val retrievedHexValues = mutableListOf<String>()

        try {
            val file = File(filesDir, filename)
            val fileReader = FileReader(file)
            val bufferedReader = BufferedReader(fileReader)

            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                retrievedHexValues.add(line!!)
            }

            bufferedReader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return retrievedHexValues.map { hexValue ->
            val binaryValue = hexToBinary(hexValue)
            val messages = generateMessage(binaryValue)
            ResultItem(hexValue, binaryValue,messages)
        }.toMutableList()
    }


    private fun saveHexToFile(hexValue: String) {
        val filename = "hex_values.txt"
        val fileContents = "$hexValue\n"

        try {
            val file = File(filesDir, filename)
            val fileWriter = FileWriter(file, true) // Open the file in append mode
            val bufferedWriter = BufferedWriter(fileWriter)

            bufferedWriter.append(fileContents)
            bufferedWriter.close()

            Toast.makeText(this, "Hexadecimal value saved to file.", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "Failed to save hexadecimal value to file.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}


data class ResultItem(val hexValue: String, val binaryValue: String, val message: String) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(hexValue)
        parcel.writeString(binaryValue)
        parcel.writeString(message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ResultItem> {
        override fun createFromParcel(parcel: Parcel): ResultItem {
            return ResultItem(parcel)
        }

        override fun newArray(size: Int): Array<ResultItem?> {
            return arrayOfNulls(size)
        }
    }
}
class ResultAdapter(private val itemClickListener: ItemClickListener, hexValues: MutableList<ResultItem>) : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {
    private val resultList: MutableList<ResultItem> = hexValues
    interface ItemClickListener {
        fun onEditItemClick(item: ResultItem,position: Int)
        fun onDeleteItemClick(item: ResultItem,position: Int)
    }
    fun addItem(item: ResultItem) {
        resultList.add(item)
        notifyDataSetChanged()
    }


    fun updateItem(position: Int, newItem: ResultItem) {
        resultList[position] = newItem
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        resultList.removeAt(position)
        notifyItemRemoved(position)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val item = resultList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    inner class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val hexTextView: TextView = itemView.findViewById(R.id.textView_hex)
        private val binaryTextView: TextView = itemView.findViewById(R.id.textView_binary)
        private val messageTextView: TextView = itemView.findViewById(R.id.textView_message)
        private val editButton: Button = itemView.findViewById(R.id.edit_button)
        private val deleteButton: Button = itemView.findViewById(R.id.delete_button)

        fun bind(item: ResultItem) {
            hexTextView.text = "Hex: ${item.hexValue}"
            binaryTextView.text = "Binary: ${item.binaryValue}"
            messageTextView.text = item.message

            editButton.setOnClickListener {
                itemClickListener.onEditItemClick(resultList[position], position);
            }

            deleteButton.setOnClickListener {
                itemClickListener.onDeleteItemClick(resultList[position],position)
            }
        }
    }
}