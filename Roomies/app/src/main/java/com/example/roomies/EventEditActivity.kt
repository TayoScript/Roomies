package com.example.roomies

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import android.os.Bundle
import android.view.View
import java.time.LocalTime

class EventEditActivity : AppCompatActivity() {
    private var eventNameET: EditText? = null
    private var eventDateTV: TextView? = null
    private var eventTimeTV: TextView? = null
    private var time: LocalTime? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_edit)
        initWidgets()
        time = LocalTime.now()
        eventDateTV!!.text =
            "Date: " + CalendarUtils.selectedDate?.let { CalendarUtils.formattedDate(it) }
        eventTimeTV!!.text = "Time: " + time
    }

    private fun initWidgets() {
        eventNameET = findViewById(R.id.eventNameET)
        eventDateTV = findViewById(R.id.eventDateTV)
        eventTimeTV = findViewById(R.id.eventTimeTV)
    }

    fun saveEventAction(view: View?) {
        val eventName = eventNameET!!.text.toString()
        val newEvent = time?.let { CalendarUtils.selectedDate?.let { it1 ->
            Event(eventName,
                it1, it)
        } }
        if (newEvent != null) {
            Event.eventsList.add(newEvent)
        }
        finish()
    }
}