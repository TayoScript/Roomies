package com.example.roomies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roomies.data.History

class AccountingAdapter(private val HistoryList : MutableList<History>) : RecyclerView.Adapter<AccountingAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_memberlist,
        parent,false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val history : History = HistoryList[position]
        holder.name.text = history.mID
        //holder.name.text = history.gID
        holder.moneyspent.text = history.moneyspent.toString()
        holder.items.text = history.mitems.toString()
    }

    override fun getItemCount(): Int {
       return HistoryList.size
    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val name : TextView
        val moneyspent : TextView
        val items : TextView
        init {
            name = itemView.findViewById(R.id.tvName)
            moneyspent = itemView.findViewById(R.id.mText)
            items = itemView.findViewById(R.id.tvItems)
        }
    }
}