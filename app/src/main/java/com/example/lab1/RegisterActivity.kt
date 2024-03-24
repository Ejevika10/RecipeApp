package com.example.lab1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val userName: EditText = findViewById(R.id.userName)
        val userSurname: EditText = findViewById(R.id.userSurname)
        val userEmail: EditText = findViewById(R.id.userEmail)
        val userPassword: EditText = findViewById(R.id.userPassword)
        val button: Button = findViewById(R.id.btnReg)
        val signInText: TextView = findViewById(R.id.loginText)

        signInText.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }
        button.setOnClickListener {
            val name = userName.text.toString().trim()
            val surname = userSurname.text.toString().trim()
            val email = userEmail.text.toString().trim()
            val password = userPassword.text.toString().trim()
            if(name == "" || surname == "" || email == "" || password == "")
                Toast.makeText(this, "Not all fields are filled in",Toast.LENGTH_SHORT).show()
            else{
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                    val db = auth.uid?.let { it1 -> database.reference.child("users").child(it1).child("userinfo") }
                    val user = User(name,surname,false,email,"","","","",false,0)
                    if (db != null) {
                        db.setValue(user).addOnCompleteListener {
                            val intent = Intent(this, ItemsActivity::class.java)
                            startActivity(intent)
                        }.addOnFailureListener {
                            Toast.makeText(this, "User registration failed: " + it.message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(this, "User registration failed", Toast.LENGTH_SHORT).show();
                    }
                }.addOnFailureListener {e ->
                    Toast.makeText(this, "User registration failed: ${e.message}", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
