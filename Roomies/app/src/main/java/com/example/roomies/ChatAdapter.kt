package com.example.roomies

import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.roomies.data.Chat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ChatAdapter(private val ChatList : MutableList<Chat>) : RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_chatlist,
        parent,false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val chat : Chat = ChatList[position]
        holder.name.text = chat.name
        holder.text.text = chat.text
        holder.time.text = chat.time
        if (chat.id == Firebase.auth.currentUser?.uid) { //your message
            holder.card.setCardBackgroundColor(Color.CYAN) //set color
            //set margins
            val params = holder.card.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(64, 8, 8, 8)
            holder.card.layoutParams = params
        }
    }

    override fun getItemCount(): Int {
       return ChatList.size
    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val name : TextView
        val text : TextView
        val time : TextView
        val card : CardView
        init {
            name = itemView.findViewById(R.id.mName)
            text = itemView.findViewById(R.id.mText)
            time = itemView.findViewById(R.id.mTime)
            card = itemView.findViewById((R.id.mCard))
        }
    }
}