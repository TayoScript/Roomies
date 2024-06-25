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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * Activity for logging in
 */
class LogIn : AppCompatActivity() {

    // UI elements
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var logInButton: Button
    private lateinit var backButton: Button

    // Firebase authentication
    private lateinit var auth: FirebaseAuth

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        // UI elements init
        emailInput = findViewById(R.id.logIn_emailInput)
        passwordInput = findViewById(R.id.logIn_passwordInput)
        logInButton = findViewById(R.id.logIn_logInButton)
        backButton = findViewById(R.id.login_backButton)
        // Firebase authentication init
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth

        // Sends the user back to the main activity
        backButton.setOnClickListener { finish() }

        // User log in
        logInButton.setOnClickListener {
            auth.signInWithEmailAndPassword(
                emailInput.text.toString(),
                passwordInput.text.toString()
            ).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    Log.d("LOG IN", "signInWithEmail:success")
                    Toast.makeText(baseContext, "Log in success.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MyGroups::class.java))

                } else {

                    Log.w("LOG IN", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    /**
     * Checks if a user is logged in or not and acts accordingly
     */
    public override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) startActivity(Intent(this, MyGroups::class.java))
    }
}

// Firebase authentication doc: https://firebase.google.com/docs/auth/android/start?authuser=1&hl=en