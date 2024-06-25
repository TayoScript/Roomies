package com.example.roomies.data

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.roomies.R
import com.example.roomies.issueModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.rpc.context.AttributeContext.Auth


class CustomAdapter(var dataList: List<issueModel>, val onClick:(Boolean, Int)->Unit ):RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    // initializing the textview and checkbox
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textView: TextView
        val checkbox: CheckBox
        val issueView: TextView
        val name: TextView
        val db = Firebase.firestore
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
        lateinit var auth: FirebaseAuth
        init {
            name = itemView.findViewById(R.id.issueName)
            textView = itemView.findViewById(R.id.issue_items)
            checkbox = itemView.findViewById(R.id.checkBox)
            issueView = itemView.findViewById(R.id.issue_text)
            issueView.visibility = View.INVISIBLE


        }
    }

    //  inflating the layout file.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemList = LayoutInflater.from(parent.context).inflate(R.layout.issue_list, parent , false)
        return ViewHolder(itemList)

    }

    //setting data to text view and our checkbox and display the data at the specified position
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = dataList[position]
        viewHolder.name.text = item.mID
        viewHolder.textView.text = item.IssueText
        viewHolder.checkbox.isChecked = item.checked


        // checking the position of the checkbox
        viewHolder.checkbox.setOnCheckedChangeListener { compoundButton, b ->
            onClick(b, position)
            // if the item is checked set it to true
            //dataList[position].checked = b
            val db = Firebase.firestore
            db.collection("issues").document(dataList[position].issueID.toString())
                .update("checked", b)
                .addOnSuccessListener {
                    Log.d("UPDATE ISSUE CLOSED", "yey")
                }
                .addOnFailureListener {
                    Log.d("UPDATE ISSUE CLOSED", "Error")
                }

            if (b){
                viewHolder.issueView.visibility = View.VISIBLE
            }
            else{
                viewHolder.issueView.visibility = View.INVISIBLE
            }

        }
    }

    //  returning our size of our list
    override fun getItemCount(): Int {
        return dataList.size
    }

    //  function to update our issueModel list when called
    fun updateData(updatedDataList: List<issueModel>){
        this.dataList = updatedDataList
        notifyDataSetChanged()
    }


}