package com.example.lab1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LogInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        auth = FirebaseAuth.getInstance()
        val userEmail: EditText = findViewById(R.id.userEmail)
        val userPassword: EditText = findViewById(R.id.userPassword)
        val button: Button = findViewById(R.id.btnLogIn)
        val registerText: TextView = findViewById(R.id.registerText)
        registerText.setOnClickListener() {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        button.setOnClickListener {
            val email = userEmail.text.toString().trim()
            val password = userPassword.text.toString().trim()
            if (email == "" || password == "")
                Toast.makeText(this, "Not all fields are filled in", Toast.LENGTH_SHORT).show()
            else {
                auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    val intent = Intent(this, ItemsActivity::class.java)
                    startActivity(intent)
                }.addOnFailureListener {
                    Toast.makeText(this, "User authentication failed: " + it.message, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
