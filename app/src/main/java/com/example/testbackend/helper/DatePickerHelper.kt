package com.example.testbackend.helper

import android.app.DatePickerDialog
import android.content.Context
import android.widget.TextView
import java.util.Calendar

class DatePickerHelper(private val context: Context) {

    fun showDatePickerDialog(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                textView.text = date
            },
            year, month, day
        )

        datePickerDialog.show()
    }
}
