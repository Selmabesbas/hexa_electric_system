package com.example.hexadecimal_electricy_counter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditHex : AppCompatActivity() {
    private lateinit var editTextHex: EditText

    private var position: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_hex)

        editTextHex = findViewById(R.id.editText_hex)
        val buttonUpdate: Button = findViewById(R.id.button_update)

        val extras = intent.extras
        if (extras != null) {
            position = extras.getInt(EXTRA_POSITION, -1)
            val item = extras.getParcelable(EXTRA_ITEM) as ResultItem?
            if (position != -1 && item != null) {
                editTextHex.setText(item.hexValue)
            }
        }

        buttonUpdate.setOnClickListener {
            val newHexValue = editTextHex.text.toString()
            val newBinaryValue = hexToBinary(newHexValue)
            val newItem = ResultItem(newHexValue, newBinaryValue, getMessage(newBinaryValue))

            val returnIntent = Intent()
            returnIntent.putExtra(EXTRA_POSITION, position)
            returnIntent.putExtra(EXTRA_ITEM, newItem)
            returnIntent.putExtra("old_value",(extras!!.getParcelable<ResultItem>(EXTRA_ITEM)!! as ResultItem).hexValue)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }


    private fun hexToBinary(hexValue: String): String {
        val decimalValue = hexValue.toLong(16)
        return decimalValue.toString(2).padStart(32, '0')
    }



    private fun getMessage(binary: String): String {
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
        for ((index, bit) in binary.withIndex()) {
            if (bit == '1') {
                val message = messages[index]
                stringBuilder.append("$message\n")
            }
        }
        return stringBuilder.toString()
    }

    companion object {
        const val EXTRA_POSITION = "position"
        const val EXTRA_ITEM = "item"
    }
}