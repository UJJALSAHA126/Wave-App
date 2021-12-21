package com.example.wave

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

class GetDatePicker(
    private val context: Context,
    private val listener: DatePickerDialog.OnDateSetListener
) {

    fun build(): DatePickerDialog {

        val cal = Calendar.getInstance()
        val cDay = cal.get(Calendar.DAY_OF_MONTH)
        val cMonth = cal.get(Calendar.MONTH)
        val cYear = cal.get(Calendar.YEAR)
        return DatePickerDialog(context, listener, cYear, cMonth, cDay)
    }

    fun build(day: Int, month: Int, year: Int): DatePickerDialog {
        return DatePickerDialog(context, listener, year, month, day)
    }
}