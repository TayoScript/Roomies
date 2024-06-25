package com.example.roomies

import android.content.Context
import android.widget.ArrayAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.roomies.CalendarUtils
import com.example.roomies.Event
import com.example.roomies.R

class EventAdapter(context: Context, events: List<Event?>?) :
    ArrayAdapter<Event?>(context, 0, events!!) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val event = getItem(position)
        if (convertView == null) convertView =
            LayoutInflater.from(context).inflate(R.layout.event_cell, parent, false)
        val eventCellTV = convertView!!.findViewById<TextView>(R.id.eventCellTV)
        val eventTitle = event!!.name + " " + CalendarUtils.formattedTime(
            event.time
        )
        eventCellTV.text = eventTitle
        return convertView
    }
}