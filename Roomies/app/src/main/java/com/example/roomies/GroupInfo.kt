package com.example.roomies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.roomies.data.Group
import com.example.roomies.data.MemberInfo
import com.example.roomies.databinding.ActivityAccountingmainBinding
import com.example.roomies.databinding.ActivityGroupInfoBinding
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * The main activity for a specific group
 */
class GroupInfo : AppCompatActivity() {

    private  lateinit var binding: ActivityGroupInfoBinding

    // UI elements
    private lateinit var title: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var groupLink: TextView
    private lateinit var backButton: Button
    private lateinit var bottomnavigation : BottomNavigationView

    // Firebase auth
    private lateinit var auth: FirebaseAuth

    // The current group containing all group information
    private lateinit var currentGroup: Group
    private lateinit var listOfMemberInfo: MutableList<MemberInfo>

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGroupInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // UI elements init
        title = findViewById(R.id.groupInfo_title)
        recyclerView = findViewById(R.id.groupInfo_rv)
        groupLink = findViewById(R.id.groupInfo_groupLink)
        backButton = findViewById(R.id.groupInfo_backButton)
        bottomnavigation = findViewById(R.id.bottomNavigationView)

        // Firebase init
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
        val db = Firebase.firestore

        // Sends the user back to MyGroups
        backButton.setOnClickListener { finish() }

        // Gets current group
        currentGroup = getGroupFromIntent()
        listOfMemberInfo = mutableListOf()
        // Group name
        title.text = currentGroup.name
        // List of members with contact info
        displayMemberInfo(db)
        // The groups join link
        groupLink.text = "Group ID/link: ${currentGroup.id}"

        // Bottom bar
        binding.bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId){
                R.id.calendar ->{
                    startActivity(Intent(this, CalendarMain::class.java))
                }
                R.id.money ->{
                    val intent = Intent(this, AccountingMain::class.java)
                    intent.putExtra("id", currentGroup.id)
                    intent.putExtra("members", currentGroup.members.toString())
                    startActivity(intent)
                }
                R.id.issue ->{
                    val intent = Intent(this, IssueMain::class.java)
                    intent.putExtra("id", currentGroup.id)
                    intent.putExtra("members", currentGroup.members.toString())
                    startActivity(intent)
                }
                R.id.chat -> {
                    val intent = Intent(this, ChatMain::class.java)
                    intent.putExtra("id", currentGroup.id)
                    intent.putExtra("members", currentGroup.members.toString())
                    startActivity(intent)
                }
            }

            true
        }

    }

    public override fun onStart() {
        super.onStart()

        // Sends the user to the main activity if they are not logged in
        if (auth.currentUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    /**
     * Gets info about the group from intent
     */
    private fun getGroupFromIntent(): Group {

        return Group(
            id = intent.getStringExtra("id"),
            name = intent.getStringExtra("name"),
            owner = intent.getStringExtra("owner"),
            members = intent.getStringExtra("members").toString()
                .removePrefix("[").removeSuffix("]")
                .split(", ").toMutableList()
        )
    }

    /**
     * Populates the RecyclerView with the members of the group with their contact info
     * @param db    Firebase database
     */
    private fun displayMemberInfo(db: FirebaseFirestore) {

        // Queries for userInfo documents of the members
        db.collection("userInfo")
            .whereIn("uid", currentGroup.members?.toList() ?: listOf())
            .get()
            .addOnSuccessListener { docs ->
                // Creates MemberInfo-objects for the documents returned
                for (doc in docs) {
                    Log.d("GROUP INFO", "${doc.get("username").toString()}, ${doc.id}")
                    listOfMemberInfo.add(MemberInfo(
                        uid = doc.get("uid").toString(),
                        username = doc.get("username").toString(),
                        email = doc.get("email").toString(),
                        phone = doc.get("phone").toString()
                    ))
                }
                // Log
                Log.d("GROUP INFO RV", "${currentGroup.name} member count: ${currentGroup.members?.size}")
                Log.d("GROUP INFO RV", "${currentGroup.name} members:      ${currentGroup.members}")
                Log.d("GROUP INFO RV", "DB documents of members found: ${docs.size()}")
                for (doc in docs) Log.d("GROUP INFO RV", "DB document member[i]: ${doc.get("username").toString()}")
                Log.d("GROUP INFO RV", "List of public members size: ${listOfMemberInfo.size}")
                for (member in listOfMemberInfo) Log.d("GROUP INFO RV", "List of members[i]: ${member}")

                // Adapter for the member contact info list
                val adapter = MemberInfoListAdapter(listOfMemberInfo)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Log.w("GROUP INFO", "Error occurred while trying to find member info", e)
            }
    }
}