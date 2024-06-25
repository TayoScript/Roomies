package com.example.roomies

import androidx.appcompat.app.AppCompatActivity
import com.example.roomies.CalendarAdapter.OnItemListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import android.content.Intent
import android.view.View
import android.widget.ListView
import java.time.LocalDate

class WeekViewActivity : AppCompatActivity(), OnItemListener {
    private var monthYearText: TextView? = null
    private var calendarRecyclerView: RecyclerView? = null
    private var eventListView: ListView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week_view)
        initWidgets()
        setWeekView()
    }

    private fun initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView)
        monthYearText = findViewById(R.id.monthYearTV)
        eventListView = findViewById(R.id.eventListView)
    }

    private fun setWeekView() {
        monthYearText!!.text =
            CalendarUtils.selectedDate?.let { CalendarUtils.monthYearFromDate(it) }
        val days = CalendarUtils.selectedDate?.let { CalendarUtils.daysInWeekArray(it) }
        val calendarAdapter = days?.let { CalendarAdapter(it, this) }
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(applicationContext, 7)
        calendarRecyclerView!!.layoutManager = layoutManager
        calendarRecyclerView!!.adapter = calendarAdapter
        setEventAdpater()
    }

    fun previousWeekAction(view: View?) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate?.minusWeeks(1)
        setWeekView()
    }

    fun nextWeekAction(view: View?) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate?.plusWeeks(1)
        setWeekView()
    }

    override fun onItemClick(position: Int, date: LocalDate?) {
        CalendarUtils.selectedDate = date
        setWeekView()
    }

    override fun onResume() {
        super.onResume()
        setEventAdpater()
    }

    private fun setEventAdpater() {
        val dailyEvents = CalendarUtils.selectedDate?.let { Event.eventsForDate(it) }
        val eventAdapter = EventAdapter(applicationContext, dailyEvents)
        eventListView!!.adapter = eventAdapter
    }

    fun newEventAction(view: View?) {
        startActivity(Intent(this, EventEditActivity::class.java))
    }
}