package com.example.roomies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Activity for the user to manage their own account
 */
class MeInfo : AppCompatActivity() {

    // UI elements
    private lateinit var backButton: Button
    private lateinit var logoutButton: Button
    private lateinit var usernameInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var saveNameButton: Button
    private lateinit var savePhoneButton: Button
    private lateinit var saveEmailButton: Button

    // Firebase auth
    private lateinit var auth: FirebaseAuth

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_me_info)

        // UI elements init
        backButton = findViewById(R.id.meInfo_backButton)
        logoutButton = findViewById(R.id.meInfo_logoutButton)
        usernameInput = findViewById(R.id.meInfo_nameEdit)
        phoneInput = findViewById(R.id.meInfo_phoneEdit)
        emailInput = findViewById(R.id.meInfo_emailEdit)
        saveNameButton = findViewById(R.id.meInfo_saveNameChangeButton)
        savePhoneButton = findViewById(R.id.meInfo_savePhoneChangeButton)
        saveEmailButton = findViewById(R.id.meInfo_saveEmailChangeButton)

        // Firebase authentication init
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
        val db = Firebase.firestore

        // Sends the user back to the MyGroups activity
        backButton.setOnClickListener {
            startActivity(Intent(this, MyGroups::class.java))
        }

        // Logs out the user and sends he/she back to the main activity
        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Setting the user information that can be changed
        usernameInput.setText(auth.currentUser?.displayName, TextView.BufferType.EDITABLE)
        getPhone(db)
        emailInput.setText(auth.currentUser?.email, TextView.BufferType.EDITABLE)

        // Save changes buttons
        saveNameButton.setOnClickListener { saveAccountInfoChange("username", usernameInput.text.toString(), db) }
        savePhoneButton.setOnClickListener { saveAccountInfoChange("phone", phoneInput.text.toString(), db) }
        saveEmailButton.setOnClickListener { saveAccountInfoChange("email", emailInput.text.toString(), db) }
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
     * Gets the phone number from the userInfo collection
     * @param db        Firebase database
     */
    private fun getPhone(db: FirebaseFirestore) {

        phoneInput.setText("loading...", TextView.BufferType.EDITABLE)

        db.collection("userInfo").whereEqualTo("uid", auth.currentUser?.uid).limit(1).get()
            .addOnSuccessListener { docs ->
                if (!docs.isEmpty) {
                    phoneInput.setText(docs.documents[0].get("phone").toString(), TextView.BufferType.EDITABLE)
                    Log.d("ME INFO", "The phone of ${auth.currentUser?.displayName} found: ${docs.documents[0].get("phone").toString()}")
                } else {
                    phoneInput.setText("404 Not found", TextView.BufferType.EDITABLE)
                    Log.d("ME INFO", "The phone of ${auth.currentUser?.displayName} not found")
                }
            }
            .addOnFailureListener { e ->
                phoneInput.setText("404 Not found", TextView.BufferType.EDITABLE)
                Log.w("ME INFO", "Error while trying to get phone number of ${auth.currentUser?.displayName}: ", e)
            }
    }

    /**
     * Updates account info
     * @param infoType  What to be updated: "username", "phone", or "email"
     * @param newValue  The new value of the given infoType
     * @param db        Firebase database
     */
    private fun saveAccountInfoChange(infoType: String, newValue: String, db: FirebaseFirestore) {

        // Updating the firebase accounts
        when (infoType) {
            "username" -> {
                auth.currentUser?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(usernameInput.text.toString()).build())
                updatePublicUserInfo(infoType, newValue, db)
                Log.d("ME INFO", "username updated")
                Toast.makeText(baseContext, "Name updated", Toast.LENGTH_SHORT).show()
            }
            "email" -> {
               auth.currentUser?.updateEmail(emailInput.text.toString())
                   ?.addOnCompleteListener { task ->
                       if (task.isSuccessful) {
                           updatePublicUserInfo(infoType, newValue, db)
                           Log.d("ME INFO", "email updated")
                           Toast.makeText(baseContext, "Email updated", Toast.LENGTH_SHORT).show()
                       } else {
                           Log.w("ME INFO", "Could not update email "+task.exception?.message.toString(), task.exception)
                           Toast.makeText(baseContext, task.exception?.message.toString(), Toast.LENGTH_LONG).show()
                       }
                   }
            }
            "phone" -> {
                updatePublicUserInfo(infoType, newValue, db)
            }
            else -> Log.d("SAVE ACCOUNT CHANGES", "Invalid 'infoType'. This message should never be seen.")
        }
    }

    /**
     * Checks if document for public user info exists and updates it if it does
     * @param infoType  What to be updated: "username", "phone", or "email"
     * @param newValue  The new value of the given infoType
     * @param db        Firebase database
     */
    private fun updatePublicUserInfo(infoType: String, newValue: String, db: FirebaseFirestore) {
        db.collection("userInfo").whereEqualTo("uid", auth.currentUser?.uid).limit(1).get()
            .addOnSuccessListener { docs ->
                if (!docs.isEmpty) updateUserInfo(docs.documents[0].id, infoType, newValue, db)
                else {
                    Log.d("ME INFO", "UserInfo document for ${auth.currentUser?.uid} does not exist")
                    Toast.makeText(baseContext, "Your account was created on an old version of this app. Create a new one", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Log.w("ME INFO", "Error while trying to update $infoType: ", e)
            }
    }

    /**
     * Updates the public user info in the userInfo collection
     * @param docID     The ID of the document to update
     * @param infoType  What to be updated: "username", "phone", or "email"
     * @param newValue  The new value of the given infoType
     * @param db        Firebase database
     */
    private fun updateUserInfo(docID: String, infoType: String, newValue: String, db: FirebaseFirestore) {
        db.collection("userInfo").document(docID).update(infoType, newValue)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) Log.d("ME INFO", "$infoType updated")
            }
            .addOnFailureListener { e ->
                Log.w("ME INFO", "Error while trying to update $infoType: ", e)
            }
    }
}