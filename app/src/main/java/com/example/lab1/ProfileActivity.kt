package com.example.lab1

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Integer.parseInt
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        loadUserInfo()

        val editBtn: ImageButton = findViewById(R.id.editBtn)
        editBtn.setOnClickListener{
            saveUserInfo()
        }
        val backBtn: ImageButton = findViewById(R.id.backBtn)
        backBtn.setOnClickListener{
            onBackPressed()
        }

    }
    private fun loadUserInfo(){
        val name:EditText = findViewById(R.id.editName)
        val surname:EditText = findViewById(R.id.editSurname)
        val genderMale:RadioButton = findViewById(R.id.maleRadioButton)
        val genderFemale:RadioButton = findViewById(R.id.femaleRadioButton)
        val email:EditText = findViewById(R.id.editEmail)
        val birthdate: DatePicker = findViewById(R.id.datePicker)
        val fav_recepie:EditText = findViewById(R.id.editFav)
        val address: EditText = findViewById(R.id.editAddress)
        val phone: EditText = findViewById(R.id.editPhone)
        val vegeterianYes:RadioButton = findViewById(R.id.vegYesRadioButton)
        val vegeterianNo:RadioButton = findViewById(R.id.vegNoRadioButton)

        val skillEasy:RadioButton = findViewById(R.id.easyRadioButton)
        val skillMedium:RadioButton = findViewById(R.id.mediumRadioButton)
        val skillHard:RadioButton = findViewById(R.id.hardRadioButton)

        val db = database.reference.child("users").child(auth.uid!!).child("userinfo")

        val profileListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    name.setText(user.name)
                    surname.setText(user.surname)
                    genderMale.isChecked = user.gender as Boolean
                    genderFemale.isChecked = !(user.gender as Boolean)
                    email.setText(user.email)
                    var day: String = "2024"
                    var month: String = "02"
                    var year: String = "29"
                    if(!user.birthdate.isNullOrEmpty()){
                        day = user.birthdate.substringBefore('.')
                        month = user.birthdate.substringBeforeLast('.')
                        month = month.substringAfter('.')
                        year = user.birthdate.substringAfterLast('.')
                    }

                    birthdate.init(year.toInt(), month.toInt(),day.toInt(),null)

                    fav_recepie.setText(user.fav_recepie)
                    address.setText(user.address)
                    phone.setText(user.phone)
                    vegeterianYes.isChecked = user.vegeterian as Boolean
                    vegeterianNo.isChecked = !(user.vegeterian as Boolean)

                    skillEasy.isChecked = (user.skill as Int == 1)
                    skillMedium.isChecked = (user.skill as Int == 2)
                    skillHard.isChecked = (user.skill as Int == 3)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
            }
        }
        db.addValueEventListener(profileListener)
    }
    private fun saveUserInfo(){
        val name:EditText = findViewById(R.id.editName)
        val surname:EditText = findViewById(R.id.editSurname)
        val genderMale:RadioButton = findViewById(R.id.maleRadioButton)
        val genderFemale:RadioButton = findViewById(R.id.femaleRadioButton)
        val email:EditText = findViewById(R.id.editEmail)
        val birthdate: DatePicker = findViewById(R.id.datePicker)
        val fav_recepie:EditText = findViewById(R.id.editFav)
        val address: EditText = findViewById(R.id.editAddress)
        val phone: EditText = findViewById(R.id.editPhone)
        val vegeterianYes:RadioButton = findViewById(R.id.vegYesRadioButton)
        val vegeterianNo:RadioButton = findViewById(R.id.vegNoRadioButton)

        val skillEasy:RadioButton = findViewById(R.id.easyRadioButton)
        val skillMedium:RadioButton = findViewById(R.id.mediumRadioButton)
        val skillHard:RadioButton = findViewById(R.id.hardRadioButton)

        val db = database.reference.child("users").child(auth.uid!!).child("userinfo")

        val day: Int = birthdate.dayOfMonth
        val month: Int = birthdate.month
        val year: Int = birthdate.year

        val skill:Int = if(skillEasy.isChecked){
            1
        } else if(skillMedium.isChecked){
            2
        } else{
            3
        }


        val user = User(name.text.toString(),surname.text.toString(),genderMale.isChecked,email.text.toString(),"$day.$month.$year",fav_recepie.text.toString(),address.text.toString(),phone.text.toString(),vegeterianYes.isChecked, skill)
        db.setValue(user).addOnCompleteListener {
        }
    }
}
