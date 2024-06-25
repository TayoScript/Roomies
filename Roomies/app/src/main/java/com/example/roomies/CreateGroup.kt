package com.example.roomies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.roomies.data.Group
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Activity for creating new groups
 */
class CreateGroup : AppCompatActivity() {

    // UI elements
    private lateinit var groupNameInput: EditText
    private lateinit var createButton: Button
    private lateinit var backButton: Button

    // Firebase authentication
    private lateinit var auth: FirebaseAuth

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        // UI elements init
        groupNameInput = findViewById(R.id.createGroup_groupNameInput)
        createButton = findViewById(R.id.createGroup_createButton)
        backButton = findViewById(R.id.createGroup_backButton)

        // Firebase authentication init
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
        val db = Firebase.firestore

        // Sends the user back to MyGroups
        backButton.setOnClickListener { finish() }

        // Creates a group and adds it to the DB
        createButton.setOnClickListener {

            // Creates a new group
            val newGroup = Group(
                //id = "",
                name = groupNameInput.text.toString(),
                owner = auth.currentUser?.uid.toString(),
                members = mutableListOf(auth.currentUser?.uid.toString())
            )

            // Adds the new group to the DB
            db.collection("groups").add(newGroup)
                .addOnCompleteListener { docRef ->
                    Log.d("CREATE GROUP", "Group added to DB with id: ${it.id}")
                    Toast.makeText(baseContext, "Group created", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MyGroups::class.java))
                }
                .addOnFailureListener { err ->
                    Log.w("CREATE GROUP", "Error while adding group to DB", err)
                    Toast.makeText(baseContext, "Group creation failed", Toast.LENGTH_SHORT).show()
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