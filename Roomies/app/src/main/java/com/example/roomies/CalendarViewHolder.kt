package com.example.roomies

import android.view.View
import com.example.roomies.CalendarAdapter.OnItemListener
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import java.time.LocalDate
import java.util.ArrayList

class CalendarViewHolder internal constructor(
    itemView: View,
    onItemListener: OnItemListener,
    days: ArrayList<LocalDate?>
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    private val days: ArrayList<LocalDate?>
    @JvmField
    val parentView: View
    @JvmField
    val dayOfMonth: TextView
    private val onItemListener: OnItemListener

    init {
        parentView = itemView.findViewById(R.id.parentView)
        dayOfMonth = itemView.findViewById(R.id.cellDayText)
        this.onItemListener = onItemListener
        itemView.setOnClickListener(this)
        this.days = days
    }

    override fun onClick(view: View) {
        onItemListener.onItemClick(adapterPosition, days[adapterPosition])
    }
}