package com.example.roomies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roomies.data.Group

/**
 * Adapter for the list of groups in the MyGroups activity the current logged in user is a member of
 */
class GroupListAdapter(var dataList: List<Group>, val onClick: (Int)->Unit): RecyclerView.Adapter<GroupListAdapter.ListItemViewHolder>() {

    inner class ListItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val groupNameTextView: TextView
        init {
            groupNameTextView = view.findViewById(R.id.myGroupsItem_groupName)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mygroups_groupitem, parent, false)
        return ListItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val item = dataList[position]
        holder.groupNameTextView.text = item.name
        // Returns the position/index of the item clicked on
        holder.groupNameTextView.setOnClickListener { aView ->
            onClick(position)
        }
    }

    override fun getItemCount() = dataList.size
}