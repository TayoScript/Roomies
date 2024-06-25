package com.example.roomies

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * The main activity
 * For logging in and signing up
 */
class MainActivity : AppCompatActivity() {

    // UI elements
    private lateinit var logInButton: Button
    private lateinit var signUpButton: Button
    private lateinit var group: Button

    // Firebase authentication
    private lateinit var auth: FirebaseAuth

    /**
     *
     */
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI elements init
        logInButton = findViewById(R.id.mainAtivity_logInButton)
        signUpButton = findViewById(R.id.mainActivity_signUpButton)

        // Firebase authentication init
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth

        // Sends the user to the LogIn activity
        logInButton.setOnClickListener {
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
        }

        // Sends the user to the SignUp activity
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }

    /**
     * Checks if a user is logged in or not and acts accordingly
     */
    public override fun onStart() {
        super.onStart()

        // Sends the user to MyGroups if he/she is logged in
        if (auth.currentUser != null) startActivity(Intent(this, MyGroups::class.java))
    }
}