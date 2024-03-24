package com.example.lab1

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewSwitcher.ViewFactory
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.lang.Integer.parseInt
import java.lang.Long

class ItemActivity : AppCompatActivity() {
    var isFavourite = false;

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    private lateinit var imageSwitcher: ImageSwitcher

    private var curMark = Long(0);

    private val imageUrls = listOf(
        "01.jpg",
        "02.jpg",
        "03.jpg",
        "04.jpg",
        "05.jpg",
        "06.jpg",
        "07.jpg"
    )
    var index = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        val name: TextView = findViewById(R.id.itemListName)
        val time: TextView = findViewById(R.id.itemListTime)
        val diff: TextView = findViewById(R.id.itemListDiff)
        val price: TextView = findViewById(R.id.itemListPrice)
        val desk: TextView = findViewById(R.id.itemListDesk)
        val mark1: ImageButton = findViewById(R.id.itemMark1)
        val mark2: ImageButton = findViewById(R.id.itemMark2)
        val mark3: ImageButton = findViewById(R.id.itemMark3)
        val mark4: ImageButton = findViewById(R.id.itemMark4)
        val mark5: ImageButton = findViewById(R.id.itemMark5)

        val item = intent.getParcelableExtra<Item>("item")

        name.text = item!!.name
        time.text = item.time
        diff.text = item.diff
        val curPrice = item.price.toString()
        price.text = "$curPrice$"
        desk.text = item.desk

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        imageSwitcher = findViewById(R.id.imageSwitcher)
        imageSwitcher.setFactory {
            val imgView = ShapeableImageView(applicationContext)
            imgView.scaleType = ImageView.ScaleType.FIT_XY
            imgView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.UNSPECIFIED_GRAVITY)
            imgView.adjustViewBounds = true
            imgView.background = ContextCompat.getDrawable(this, R.drawable.bg_rounded_corner)
            imgView
        }

        if(item.images != null){
            val storageRef = storage.reference.child(item.images)
            displayImageFromFirebase(storageRef,imageUrls[index])

            imageSwitcher.setOnClickListener{
                index++
                if(index == 7)
                    index = 0
                displayImageFromFirebase(storageRef,imageUrls[index])
            }
        }
        else{
            val storageRef = storage.reference.child("img")
            if (item.img != null) {
                displayImageFromFirebase(storageRef,item.img)
            }
        }
        val favBtn: ImageButton = findViewById(R.id.itemAddFavourites)
        favBtn.setOnClickListener{
            if(auth.currentUser != null){
                if(isFavourite){
                    removeFromFavourites(item)
                }
                else{
                    addToFavourites(item)
                }
            }
        }
        if (auth.currentUser != null){
            val itemId = item.id as String
            val dbFav = database.reference.child("users").child(auth.uid!!).child("favourites").child(itemId)
                .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            favBtn.setBackgroundResource(R.drawable.heart_filled)
                            isFavourite = true
                        }
                        else{
                            favBtn.setBackgroundResource(R.drawable.heart)
                            isFavourite = false
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                }
                )
        }
        if (auth.currentUser != null){
            val itemId = item.id as String
            val dbMarks = database.reference.child("items").child(itemId).child("ratings").child(auth.uid!!)
                .addValueEventListener(object: ValueEventListener {
                @SuppressLint("UseValueOf")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val rating = snapshot.value
                        curMark = rating as Long
                        if (rating == Long (5)) {
                            mark1.setBackgroundResource(R.drawable.star_filled)
                            mark2.setBackgroundResource(R.drawable.star_filled)
                            mark3.setBackgroundResource(R.drawable.star_filled)
                            mark4.setBackgroundResource(R.drawable.star_filled)
                            mark5.setBackgroundResource(R.drawable.star_filled)
                        }else if(rating == Long (4)){
                            mark1.setBackgroundResource(R.drawable.star_filled)
                            mark2.setBackgroundResource(R.drawable.star_filled)
                            mark3.setBackgroundResource(R.drawable.star_filled)
                            mark4.setBackgroundResource(R.drawable.star_filled)
                            mark5.setBackgroundResource(R.drawable.star)
                        }else if(rating == Long (3)){
                            mark1.setBackgroundResource(R.drawable.star_filled)
                            mark2.setBackgroundResource(R.drawable.star_filled)
                            mark3.setBackgroundResource(R.drawable.star_filled)
                            mark4.setBackgroundResource(R.drawable.star)
                            mark5.setBackgroundResource(R.drawable.star)
                        }else if(rating == Long (2)){
                            mark1.setBackgroundResource(R.drawable.star_filled)
                            mark2.setBackgroundResource(R.drawable.star_filled)
                            mark3.setBackgroundResource(R.drawable.star)
                            mark4.setBackgroundResource(R.drawable.star)
                            mark5.setBackgroundResource(R.drawable.star)
                        }else if(rating == Long (1)){
                            mark1.setBackgroundResource(R.drawable.star_filled)
                            mark2.setBackgroundResource(R.drawable.star)
                            mark3.setBackgroundResource(R.drawable.star)
                            mark4.setBackgroundResource(R.drawable.star)
                            mark5.setBackgroundResource(R.drawable.star)
                        }else{
                            mark1.setBackgroundResource(R.drawable.star)
                            mark2.setBackgroundResource(R.drawable.star)
                            mark3.setBackgroundResource(R.drawable.star)
                            mark4.setBackgroundResource(R.drawable.star)
                            mark5.setBackgroundResource(R.drawable.star)
                        }

                    } else {
                        curMark = Long(0)
                        mark1.setBackgroundResource(R.drawable.star)
                        mark2.setBackgroundResource(R.drawable.star)
                        mark3.setBackgroundResource(R.drawable.star)
                        mark4.setBackgroundResource(R.drawable.star)
                        mark5.setBackgroundResource(R.drawable.star)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("Ошибка при чтении данных: ${databaseError.message}")
                }
            })
        }


        mark1.setOnClickListener{
            addRating(item,1)
        }
        mark2.setOnClickListener{
            addRating(item,2)
        }
        mark3.setOnClickListener{
            addRating(item,3)
        }
        mark4.setOnClickListener{
            addRating(item,4)
        }
        mark5.setOnClickListener{
            addRating(item,5)
        }
        val commentBtn: ImageButton = findViewById(R.id.commentBtn)
        commentBtn.setOnClickListener{
            val intent = Intent(this, CommentsActivity::class.java)
            intent.putExtra("itemId", item.id)
            startActivity(intent)
        }
        val backBtn: ImageButton = findViewById(R.id.backBtn)
        backBtn.setOnClickListener{
            onBackPressed()
        }
    }

    @SuppressLint("UseValueOf")
    private fun addRating(item: Item, mark: Int){
        val itemId = item.id
        val db = database.reference.child("items").child(itemId!!).child("ratings").child(auth.uid!!).setValue(mark)
            .addOnSuccessListener {
            Toast.makeText(this, "Your rating has been added", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this,"Something goes wrong. Your rating hasn't been added",Toast.LENGTH_SHORT).show()
            }
        if(curMark == Long(0)){
            var num = item.numOfRatings as Int
            var sum = item.avgRating?.times(num)
            num = num!! + 1
            sum = sum?.plus(mark.toFloat())
            var avg = sum?.div(num)
            val db1 = database.reference.child("items").child(itemId).child("numOfRatings").setValue(num)
            val db2 = database.reference.child("items").child(itemId).child("avgRating").setValue(avg)
        }
        else{
            var num = item.numOfRatings as Int
            var sum = item.avgRating?.times(num)
            sum = sum?.minus(curMark.toFloat())
            sum = sum?.plus(mark)
            var avg = sum?.div(num)
            val db1 = database.reference.child("items").child(itemId).child("avgRating").setValue(avg)
        }
    }

    private fun addToFavourites(item: Item){
        val timestamp = System.currentTimeMillis()
        val hashMap = HashMap<String, Any>()
        val itemId = item.id
        hashMap["itemId"] = itemId as String
        hashMap["timestamp"] = timestamp
        val itemName = item.name

        val db = database.reference.child("users").child(auth.uid!!).child("favourites").child(itemId).setValue(hashMap).addOnSuccessListener {
            Toast.makeText(this, "$itemName has been added to favourites", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this, "Something goes wrong. $itemName hasn't been added to favourites", Toast.LENGTH_SHORT).show()
        }
        isFavourite = true

    }
    private fun removeFromFavourites(item: Item){
        val itemId = item.id as String
        val itemName = item.name

        val db = database.reference.child("users").child(auth.uid!!).child("favourites").child(itemId).removeValue()
            .addOnSuccessListener { Toast.makeText(this, "Something goes wrong. $itemName hasn't been removed from favourites", Toast.LENGTH_SHORT).show(); }
        isFavourite = false
    }


    private fun displayImageFromFirebase(storageRef: StorageReference, imageUrl:String) {
        val imageRef = storageRef.child(imageUrl)

        val localFile = File.createTempFile("images", "jpg")
        imageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            imageSwitcher.setImageDrawable(BitmapDrawable(resources, bitmap))
        }.addOnFailureListener { exception ->
            Toast.makeText(this,"!!!",Toast.LENGTH_SHORT).show()
        }
    }

}