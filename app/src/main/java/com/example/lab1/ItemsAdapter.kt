package com.example.lab1

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

import org.w3c.dom.Text
import java.io.File
import java.lang.Long

class ItemsAdapter(var items: List<Item>, var context: Context):RecyclerView.Adapter<ItemsAdapter.MyViewHolder>() {
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    var onItemClick : ((Item) -> Unit)? = null

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val image: ImageView = view.findViewById(R.id.itemListImage)
        val name: TextView = view.findViewById(R.id.itemListName)
        val time: TextView = view.findViewById(R.id.itemListTime)
        val diff: TextView = view.findViewById(R.id.itemListDiff)
        val price: TextView = view.findViewById(R.id.itemListPrice)
        val avgRating:TextView = view.findViewById(R.id.itemAvgRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_in_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  items.count()
    }

    @SuppressLint("SetTextI18n", "UseValueOf")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.text = items[position].name
        holder.time.text = items[position].time
        holder.diff.text = items[position].diff
        val curPrice = items[position].price.toString()
        holder.price.text = "$curPrice$"
        if( items[position].avgRating != null)
            holder.avgRating.text = items[position].avgRating.toString()
        else
            holder.avgRating.text = "0.0"

        val link: String? = items[position].img

        getImage(
            imagePath = link,
            onSuccess = { file ->
                val imageUri = Uri.fromFile(file)
                holder.image.setImageURI(imageUri)
            },
            onFailure = { errorMessage ->

            })

        holder.itemView.setOnClickListener{
            onItemClick?.invoke(items[position])
        }
    }
    fun getImage(imagePath: String?, onSuccess: (File) -> Unit, onFailure: (String) -> Unit) {
        imagePath?.let { path ->
            storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference.child("img/$path")

            val localFile = File.createTempFile("tempImage", "jpg")

            storageRef.getFile(localFile)
                .addOnSuccessListener {
                    onSuccess(localFile)
                }
                .addOnFailureListener { exception ->
                    onFailure(exception.message ?: "Failed to fetch image")
                }
        } ?: run {
            onFailure("Image path is null")
        }
    }
}