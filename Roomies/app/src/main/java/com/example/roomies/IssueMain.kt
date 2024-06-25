package com.example.roomies

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.roomies.data.CustomAdapter
import com.example.roomies.data.Group
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class IssueMain : AppCompatActivity() {

    lateinit var addbutton: Button
    lateinit var backButton: Button
    lateinit var additem: EditText
    lateinit var recyclerView: RecyclerView

    private lateinit var auth: FirebaseAuth

    lateinit var currentGroup: Group
    lateinit var issueAdapter: CustomAdapter
    val issueList = mutableListOf<issueModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue)

        FirebaseApp.initializeApp(this)
        val db = Firebase.firestore
        auth = Firebase.auth

        currentGroup = getGroupFromIntent()

        addbutton = findViewById(R.id.addbutton)
        backButton = findViewById(R.id.issue_backbutton)
        additem = findViewById(R.id.additem)
        recyclerView = findViewById(R.id.listview)
  
        // send arguments to the public constructor customAdapter
        issueAdapter = CustomAdapter(issueList){isChecked, position ->
                issueList[position].checked = isChecked
        }
        recyclerView.adapter = issueAdapter

        getMyIssues(db)

        backButton.setOnClickListener { finish() }
    
        addbutton.setOnClickListener {

            val issuetext = additem.text.toString()
            additem.setText("")

            if (issuetext.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "Fill out issue ", Toast.LENGTH_SHORT).show()
            } else {

                val issueModel =
                    issueModel(
                        gID = currentGroup.id,
                        mID = auth.currentUser?.uid.toString(),
                        IssueText =  issuetext,
                        checked = false
                    )

                issueList.add(issueModel)
                issueAdapter.updateData(issueList)

                db.collection("issues").add(issueModel)
                    .addOnCompleteListener { docRef ->
                        Log.d("NEW ISSUE EVENT", "Event added to DB: ${it.id}")
                        Toast.makeText(baseContext, "issue added", Toast.LENGTH_SHORT).show()
                        issueModel.issueID = it.id.toString()
                    }
                    .addOnFailureListener { err ->
                        Log.w("NEW ISSUE EVENT", "Error adding new event to DB", err)
                        Toast.makeText(baseContext, "Event creation failed", Toast.LENGTH_SHORT).show()
                    }

            }
        }

        /* editText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
             if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                 val issueModel = issueModel(editText.text.toString(), false)
                 issueList.add(issueModel)
                 adapter.updateData(issueList)
                 return@OnKeyListener true
             }
             false
         })*/

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
     *  Finds group in db and fetches data
     */
    private fun getGroupFromIntent(): Group {

        return Group(
            id = intent.getStringExtra("id"),
            //name = intent.getStringExtra("name"),
            //owner = intent.getStringExtra("owner"),
            members = intent.getStringExtra("members").toString()
                .removePrefix("[").removeSuffix("]")
                .split(", ").toMutableList()
        )
    }

    /**
     * Returns the groups the authenticated user is a member of
     * @param db    Firebase database
     */
   private fun getMyIssues(db: FirebaseFirestore) {

        // Queries for group documents where the current user is a member
        db.collection("issues")
            .whereEqualTo("gid", currentGroup.id)
            .get()
            .addOnSuccessListener { docs ->
                // Creates Group-objects for the documents returned
                for (doc in docs) {
                    Log.d("USER ACCOUNTING DB", "${doc.get("name").toString()}, ${doc.id}")
                    issueList.add(issueModel(
                        issueID = doc.id,
                        gID = doc.get("gid").toString(),
                        mID = doc.get("mid").toString(),
                        IssueText = doc.get("issueText").toString(),
                        checked = doc.get("checked") as Boolean 
                    )
                    )
                    Log.d("GETTING NEW ISSUE", doc.id)
                }
                // Updating the adapter
                issueAdapter.updateData(issueList)
            }
            .addOnFailureListener { e ->
                Log.w("USER ACCOUNTING", "Error occurred while trying to get user accounting", e)
                Toast.makeText(baseContext, "Could not find any user accounting", Toast.LENGTH_SHORT).show()
            }
    }
}

