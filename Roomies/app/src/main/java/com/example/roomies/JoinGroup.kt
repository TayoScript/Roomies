package com.example.roomies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.roomies.data.Group
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

/**
 * Activity for joining groups
 */
class JoinGroup : AppCompatActivity() {

    // UI elements
    private lateinit var groupIDinput: EditText
    private lateinit var joinButton: Button
    private lateinit var backButton: Button

    // Firebase authentication
    private lateinit var auth: FirebaseAuth

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_group)

        // UI elements init
        groupIDinput = findViewById(R.id.joinGroup_groupIDinput)
        joinButton = findViewById(R.id.joinGroup_joinButton)
        backButton = findViewById(R.id.joinGroup_backButton)

        // Firebase authentication init
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
        val db = Firebase.firestore

        // Sends the user back to MyGroups
        backButton.setOnClickListener { finish() }

        // Adds the current user to the group with the given group ID
        joinButton.setOnClickListener {

            // If group ID is not provided
            if (groupIDinput.text.toString().isEmpty()) {
                Log.d("JOIN GRPUP", "No group ID was given")
                Toast.makeText(baseContext, "No group ID was provided", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tries to find the group with the given ID
            val document = db.collection("groups").document(groupIDinput.text.toString())
            document.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val doc = task.result

                    // Checks if the document exists
                    if (doc != null && doc.exists()) {
                        val groupName = doc.get("name").toString()
                        Log.d("JOIN GROUP", "Document ${groupName}} found.")

                        // Gets the UIDs of the members in the group
                        val stringOfMembers = doc.get("members").toString().removePrefix("[").removeSuffix("]")
                        val groupMembers = stringOfMembers.split(", ").toMutableList()

                        Log.d("GET____DOC", "Name: ${groupName}")

                        // Checks if the current user already is a member or not

                        if (!groupMembers.contains(auth.currentUser?.uid)) {
                            // Fetches current user logged in
                            // auth.currentUser?.displayName -> uid = id, can also use displayName to get username

                            // Adds the user to the group
                            groupMembers.add(auth.currentUser?.uid ?: "")
                            document.update("members", groupMembers)
                                .addOnSuccessListener {
                                    Log.d("JOIN GROUP", "User ${auth.currentUser?.uid} joined group ${doc.id}")
                                    Toast.makeText(baseContext, "Group joined", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, MyGroups::class.java))
                                }.addOnFailureListener {
                                    Log.d("JOIN GROUP", "error: ${it}")
                                    Toast.makeText(baseContext, "Error", Toast.LENGTH_SHORT).show()
                                }

                        } else {

                            // Notifies the user that he/she already is a member of the group
                            Log.d("JOIN GROUP", "User ${auth.currentUser?.uid} already is a member of ${doc.id}")
                            Toast.makeText(baseContext, "You are already a member of the group", Toast.LENGTH_SHORT).show()
                        }
                    } else {

                        // Notifies the user that the group does not exist
                        Log.d("JOIN GROUP", "Document not found.")
                        Toast.makeText(baseContext, "Group not found", Toast.LENGTH_SHORT).show()
                    }
                } else {

                    Log.d("JOIN GROUP", "Error")
                    Toast.makeText(baseContext, "Error", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { err ->
                Log.d("JOIN GROUP", "Error: "+err.message)
            }

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
}