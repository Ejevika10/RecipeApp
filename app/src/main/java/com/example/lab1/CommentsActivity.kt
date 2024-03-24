package com.example.lab1

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CommentsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var comments: kotlin.collections.ArrayList<Comment>
    lateinit var adapter: CommentsAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val arguments = intent.extras
        val itemId = arguments!!["itemId"].toString()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val db = database.reference.child("items").child(itemId).child("comments")

        val commentsList: RecyclerView = findViewById(R.id.commentsList)
        comments = arrayListOf<Comment>()

        commentsList.layoutManager = LinearLayoutManager(this)
        adapter = CommentsAdapter(comments,this)
        commentsList.adapter = adapter

        val itemListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(!comments.isEmpty())
                    comments.clear()
                loadComments(dataSnapshot)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Item failed, log a message

            }
        }
        db.addValueEventListener(itemListener)

        val editComment: EditText = findViewById(R.id.editComment)
        val sendCommentBtn: ImageButton = findViewById(R.id.sendCommentBtn)
        sendCommentBtn.setOnClickListener{
            if(auth.currentUser != null){
                val commentId = db.push().key
                val commentText = editComment.text.toString()
                var userName :String = "name"

                val db1 = database.reference.child("users").child(auth.uid!!).child("userinfo")
                db1.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val name = dataSnapshot.child("name").value as String
                        val surname = dataSnapshot.child("surname").value as String
                        userName = "$name $surname"

                        val comment = Comment(commentId,itemId,auth.uid,userName,commentText,"${System.currentTimeMillis()}")
                        if (commentId != null) {
                            db.child(commentId).setValue(comment).addOnSuccessListener {

                            }.addOnFailureListener {

                            }

                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
                Toast.makeText(this, "Your comment has been added", Toast.LENGTH_SHORT).show()
            }


        }
        val backBtn: ImageButton = findViewById(R.id.backBtn)
        backBtn.setOnClickListener{
            onBackPressed()
        }


    }
    @SuppressLint("NotifyDataSetChanged")
    private fun loadComments(dataSnapshot: DataSnapshot) {
        if(dataSnapshot.exists()){
            for(i in dataSnapshot.children) {
                val item = i.getValue(Comment::class.java)
                comments.add(item!!)
            }
            adapter.notifyDataSetChanged()
        }
    }
}