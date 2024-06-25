package com.example.roomies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roomies.data.Group
import com.example.roomies.data.MemberInfo

/**
 * Adapter for the list of members and their contact information in the GroupInfo activity
 */
class MemberInfoListAdapter(var dataList: MutableList<MemberInfo>): RecyclerView.Adapter<MemberInfoListAdapter.ListItemViewHolder>() {

    inner class ListItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val memberName: TextView
        val memberPhone: TextView
        val memberEmail: TextView
        init {
            memberName = view.findViewById(R.id.memberInfo_name)
            memberPhone = view.findViewById(R.id.memberInfo_phone)
            memberEmail = view.findViewById(R.id.memberInfo_email)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberInfoListAdapter.ListItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.member_info, parent, false)
        return ListItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberInfoListAdapter.ListItemViewHolder, position: Int) {
        val item = dataList[position]
        holder.memberName.text = item.username
        holder.memberPhone.text = item.phone
        holder.memberEmail.text = item.email
    }

    override fun getItemCount() = dataList.size
}