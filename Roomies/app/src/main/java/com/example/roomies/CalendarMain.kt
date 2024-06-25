package com.example.roomies

import androidx.appcompat.app.AppCompatActivity
import com.example.roomies.CalendarAdapter.OnItemListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import android.content.Intent
import android.view.View
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

/**
 *
 */
class CalendarMain : AppCompatActivity(), OnItemListener {

    private var monthYearText: TextView? = null
    private var calendarRecyclerView: RecyclerView? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase authentication init
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth

        setContentView(R.layout.activity_calendermain)
        initWidgets()
        CalendarUtils.selectedDate = LocalDate.now()
        setMonthView()
    }

    private fun initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView)
        monthYearText = findViewById(R.id.monthYearTV)
    }

    private fun setMonthView() {
        monthYearText!!.text =
            CalendarUtils.selectedDate?.let { CalendarUtils.monthYearFromDate(it) }
        val daysInMonth = CalendarUtils.daysInMonthArray(CalendarUtils.selectedDate)
        val calendarAdapter = CalendarAdapter(daysInMonth, this)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(applicationContext, 7)
        calendarRecyclerView!!.layoutManager = layoutManager
        calendarRecyclerView!!.adapter = calendarAdapter
    }

    fun previousMonthAction(view: View?) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate?.minusMonths(1)
        setMonthView()
    }

    fun nextMonthAction(view: View?) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate?.plusMonths(1)
        setMonthView()
    }

    override fun onItemClick(position: Int, date: LocalDate?) {
        if (date != null) {
            CalendarUtils.selectedDate = date
            setMonthView()
        }
    }

    fun weeklyAction(view: View?) {
        startActivity(Intent(this, WeekViewActivity::class.java))
    }

    /**
     * Checks if a user is logged in or not and acts accordingly
     */
    public override fun onStart() {
        super.onStart()

        // Sends the user to the login activity if they are not logged in
        if (auth.currentUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}