package com.example.roomies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.roomies.data.MemberInfo
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Activity for signing up
 */
class SignUp : AppCompatActivity() {

    // UI elements
    private lateinit var emailInput: EditText
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var passwordInputConfirm: EditText
    private lateinit var signUpButton: Button
    private lateinit var backButton: Button

    // Firebase authentication
    private lateinit var auth: FirebaseAuth

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // UI elements init
        emailInput = findViewById(R.id.signUp_emailInput)
        usernameInput = findViewById(R.id.signUp_usernameInput)
        passwordInput = findViewById(R.id.signUp_passwordInput)
        passwordInputConfirm = findViewById(R.id.signUp_passwordInputConfirm)
        signUpButton = findViewById(R.id.signUp_signUpButton)
        backButton = findViewById(R.id.signUp_backButton)

        // Firebase authentication init
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth

        // Sends the user back to the main activity
        backButton.setOnClickListener { finish() }

        // Registers a user
        signUpButton.setOnClickListener { signUpUser() }
    }

    /**
     * Checks if a user is logged in or not and acts accordingly
     */
    public override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) {
            startActivity(Intent(this, MyGroups::class.java))
        }
    }

    /**
     * Registers a new user
     */
    private fun signUpUser() {

        // Checks if the passwords match or not
        if (passwordInput.text.toString() != passwordInputConfirm.text.toString()) {
            Log.d("SIGN UP", "passwords not matching")
            Toast.makeText(baseContext, "Passwords are not matching", Toast.LENGTH_SHORT).show()
            return
        }

        // User sign up
        auth.createUserWithEmailAndPassword(
            emailInput.text.toString(),
            passwordInput.text.toString()
        ).addOnCompleteListener(this) {task ->

            if (task.isSuccessful) {

                Log.d("SIGN UP", "createUserWithEmail:success")
                Toast.makeText(baseContext, "Sign up success.", Toast.LENGTH_SHORT).show()
                // Updates/sets the username
                auth.currentUser?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(usernameInput.text.toString()).build())
                Log.d("SIGN UP", "username set/updated")

                savePublicUserInfo()

                startActivity(Intent(this, MyGroups::class.java))

            } else {

                Log.w("SIGN UP", "createUserWithEmail:failure"+task.exception?.message.toString(), task.exception)
                // Tells the user what went wrong with the authentication
                Toast.makeText(baseContext, task.exception?.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Adds public user info to DB
     */
    private fun savePublicUserInfo() {

        // DB connection
        val db = Firebase.firestore

        // Data to be added to BD
        val userInfo = MemberInfo(
            uid = auth.currentUser?.uid,
            username = usernameInput.text.toString(), // username in the auth part does not update fast enough :P
            email = auth.currentUser?.email,
            phone = ""
        )

        // Adding data to DB
        db.collection("userInfo").add(userInfo)
            .addOnSuccessListener { docRef ->
                Log.d("SIGN UP", "Public user info saved in: ${docRef.id}")
            }
            .addOnFailureListener { err ->
                Log.w("SIGN UP", "Error while adding user info to DB", err)
            }
    }
}

// Firebase authentication doc: https://firebase.google.com/docs/auth/android/start?authuser=1&hl=en