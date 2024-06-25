package com.example.roomies

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.roomies.data.Group
import com.example.roomies.databinding.ActivityGroupInfoBinding
import com.example.roomies.databinding.ActivityMyGroupsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Activity that lists all the groups the user are a member of.
 * Can navigate to CreateGroup, JoinGroup and MeInfo from here.
 */
class MyGroups : AppCompatActivity() {

    // UI elements
    private lateinit var groupListRecyclerView: RecyclerView
    private  lateinit var binding: ActivityMyGroupsBinding
    private lateinit var bottomnavigationg : BottomNavigationView

    // Firebase auth
    private lateinit var auth: FirebaseAuth

    var listOfGroups = mutableListOf<Group>()

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_groups)

        binding = ActivityMyGroupsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // UI elements init
        groupListRecyclerView = findViewById(R.id.myGroups_rv)
        bottomnavigationg = findViewById(R.id.bottomNavigationView2)

        // Firebase authentication init
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
        val db = Firebase.firestore

        getMyGroups(db)
        Log.d("MY GROUP CUNT", listOfGroups.size.toString())
        for (item in listOfGroups) {
            Log.d("MY GROUPS", item.name ?: "Error")
        }


        binding.bottomNavigationView2.setOnItemSelectedListener {

            when (it.itemId){

                R.id.Join ->  startActivity(Intent(this, JoinGroup::class.java))
                R.id.Create ->  startActivity(Intent(this, CreateGroup::class.java))
                R.id.Me -> startActivity(Intent(this, MeInfo::class.java))

            }
            true
        }
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

    /**
     * Returns the groups the authenticated user is a member of
     * @param db    Firebase database
     */
    private fun getMyGroups(db: FirebaseFirestore) {

        // Queries for group documents where the current user is a member
        db.collection("groups")
            .whereArrayContainsAny("members", listOf(auth.currentUser?.uid))
            .get()
            .addOnSuccessListener { docs ->
                // Creates Group-objects for the documents returned
                for (doc in docs) {
                    Log.d("MY GROUPS DB", "${doc.get("name").toString()}, ${doc.id}")
                    listOfGroups.add(Group(
                        id = doc.id,
                        name = doc.get("name").toString(),
                        owner = doc.get("owner").toString(),
                        members = doc.get("members").toString()
                            .removePrefix("[").removeSuffix("]")
                            .split(", ").toMutableList()
                    ))
                }
                // Adapter for the group list
                var adapter = GroupListAdapter(listOfGroups) { position ->
                    // Returns the position/index of the item clicked on
                    goToGroup(position)
                }
                groupListRecyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Log.w("MY GROUPS", "Error occurred while trying to find groups", e)
                Toast.makeText(baseContext, "Could not find your groups", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Sends the user to the activity for the group clicked on
     */
    private fun goToGroup(i: Int) {

        val intent = Intent(this, GroupInfo::class.java)

        intent.putExtra("id", listOfGroups[i].id)
        intent.putExtra("name", listOfGroups[i].name)
        intent.putExtra("owner", listOfGroups[i].owner)
        intent.putExtra("members", listOfGroups[i].members.toString())

        startActivity(intent)
    }
}