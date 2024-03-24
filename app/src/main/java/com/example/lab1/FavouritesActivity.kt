package com.example.lab1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class FavouritesActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    private lateinit var items: kotlin.collections.ArrayList<Item>
    lateinit var adapter: ItemsAdapter


    override fun onResume() {
        super.onResume()
        setContentView(R.layout.activity_favourites)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        val itemsList: RecyclerView = findViewById(R.id.itemsList)
        items = arrayListOf<Item>()
        val db = database.reference.child("users").child(auth.uid!!).child("favourites")

        itemsList.layoutManager = LinearLayoutManager(this)
        adapter = ItemsAdapter(items,this)
        itemsList.adapter = adapter

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(!items.isEmpty())
                    items.clear()
                loadItemList(dataSnapshot)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Item failed, log a message

            }
        })

        adapter.onItemClick = {
            val intent = Intent(this, ItemActivity::class.java)
            intent.putExtra("item", it)
            startActivity(intent)
        }

        val profileBtn: ImageButton = findViewById(R.id.profileBtn)
        profileBtn.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        val backBtn: ImageButton = findViewById(R.id.backBtn)
        backBtn.setOnClickListener{
            onBackPressed()
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        val itemsList: RecyclerView = findViewById(R.id.itemsList)
        items = arrayListOf<Item>()
        val db = database.reference.child("users").child(auth.uid!!).child("favourites")

        itemsList.layoutManager = LinearLayoutManager(this)
        adapter = ItemsAdapter(items,this)
        itemsList.adapter = adapter

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(!items.isEmpty())
                    items.clear()
                loadItemList(dataSnapshot)
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        adapter.onItemClick = {
            val intent = Intent(this, ItemActivity::class.java)
            intent.putExtra("item", it)
            startActivity(intent)
        }

        val profileBtn: ImageButton = findViewById(R.id.profileBtn)
        profileBtn.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        val backBtn: ImageButton = findViewById(R.id.backBtn)
        backBtn.setOnClickListener{
            onBackPressed()
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun loadItemList(dataSnapshot: DataSnapshot) {
        if(dataSnapshot.exists()){
            for(i in dataSnapshot.children) {
                val res = i.getValue() as HashMap<*, *>
                val itemId = res["itemId"] as String

                val db = database.reference.child("items").child(itemId)

                db.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val item = snapshot.getValue(Item::class.java)
                        items.add(item!!)
                        adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
    }
}


