package com.example.lab1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentsAdapter(var comments: List<Comment>, var context: Context):RecyclerView.Adapter<CommentsAdapter.MyViewHolder>()  {
    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val name: TextView = view.findViewById(R.id.authorName)
        val comment: TextView = view.findViewById(R.id.authorComment)
        val date: TextView = view.findViewById(R.id.commentDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_in_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return comments.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.text = comments[position].userName
        holder.comment.text = comments[position].comment
        val timestamp = comments[position].timestamp?.toLong()

        val date = timestamp?.let { Date(it) }
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.date.text = date?.let { sdf.format(it) }
    }
}