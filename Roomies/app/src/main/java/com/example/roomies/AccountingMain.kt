package com.example.roomies

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomies.data.Group
import com.example.roomies.data.History
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Integer

/**
 *
 */
class AccountingMain : AppCompatActivity() {

    private lateinit var rv_userList: RecyclerView
    private lateinit var historyArrayList: MutableList<History>
    private lateinit var AccountingAdapter: AccountingAdapter

    private lateinit var updateAccounting: EditText
    private lateinit var updateItem: EditText
    private lateinit var updateButton: Button
    private lateinit var backButton: Button

    // Firebase authentication
    private lateinit var auth: FirebaseAuth

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accountingmain)

        FirebaseApp.initializeApp(this)
        val db = Firebase.firestore
        auth = Firebase.auth
        val groupID = getGroupFromIntent().id
        getMyHistory(db)

        rv_userList = findViewById(R.id.rv_userList)
        rv_userList.layoutManager = LinearLayoutManager(this)
        rv_userList.setHasFixedSize(true)

        updateButton = findViewById(R.id.updateButton)
        updateAccounting = findViewById(R.id.updateAccounting)
        updateItem = findViewById(R.id.updateItem)
        backButton = findViewById(R.id.back_button)

        historyArrayList = mutableListOf()
        AccountingAdapter = AccountingAdapter(historyArrayList)
        rv_userList.adapter = AccountingAdapter

        /*
         * Rough estimate over how it should work
         * */
        updateButton.setOnClickListener {
            if (validateFields()) {
                val moneyspent = updateAccounting.text.toString().toInt()
                val item_bought = updateItem.text.toString()
                    val newHistory = History(
                        gID = groupID,
                        mID = auth.currentUser?.uid.toString(),
                        moneyspent = moneyspent,
                        mitems = item_bought
                    )
                // Empties text field after values are updated to DB
                updateAccounting.setText("")
                updateItem.setText("")
                    db.collection("moneyspent").add(newHistory)
                        .addOnCompleteListener { docRef ->
                            Log.d("NEW ACCOUNTING EVENT", "Event added to DB: ${it.id}")
                            Toast.makeText(
                                baseContext,
                                "Accounting event added",
                                Toast.LENGTH_SHORT
                            ).show()
                            historyArrayList.add(newHistory)
                        }
                        .addOnFailureListener { err ->
                            Log.w("NEW ACCOUNTING EVENT", "Error adding new event to DB", err)
                            Toast.makeText(baseContext, "Event creation failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                //  EventChangeListener()
            }
        }
        backButton.setOnClickListener { finish() }
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
     * Function to check whether or not there is input in edittexts
     */
     fun validateFields(): Boolean {
        var minLength = 1;
        if (updateAccounting.getText().length < minLength) {
            updateAccounting.setError("Input is missing here");
            return false;
        }else if (updateItem.getText().length < minLength) {
            updateItem.setError("Input is missing here");
            return false;
        }
        else {
            return true;
        }
    }

    /**
     *  Finds group in db and fetches data (not db?)
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
    private fun getMyHistory(db: FirebaseFirestore) {

        // Queries for group documents where the current user is a member
        db.collection("moneyspent")
            .whereEqualTo("mid", auth.currentUser?.uid)// Must be uid to get data
            .get()
            .addOnSuccessListener { docs ->
                // Creates Group-objects for the documents returned
                for (doc in docs) {
                    Log.d("USER ACCOUNTING DB", "${doc.get("name").toString()}, ${doc.id}")
                    historyArrayList.add(History(
                        gID = doc.get("gid").toString(),
                        mID = doc.get("mid").toString(),
                        mitems = doc.get("mitems").toString(),
                        moneyspent = doc.getLong("moneyspent")?.toInt()
                    ))
                }
                // Adapter for the group list
                var adapter = AccountingAdapter(historyArrayList)
                rv_userList.adapter = adapter
            }
            .addOnFailureListener { e ->
                Log.w("USER ACCOUNTING", "Error occurred while trying to get user accounting", e)
                Toast.makeText(baseContext, "Could not find any user accounting", Toast.LENGTH_SHORT).show()
            }
    }
}





