package com.example.lab1

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.RadioButton
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.Locale


class ItemsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    private lateinit var items: kotlin.collections.ArrayList<Item>
    private lateinit var itemsRep: kotlin.collections.ArrayList<Item>

    lateinit var adapter: ItemsAdapter

    var difficuly:String = "all"
    var ratingDirectionUp: Boolean? = null
    var moneyDirectionUp: Boolean? = null

        @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()



        val itemsList: RecyclerView = findViewById(R.id.itemsList)
        items = arrayListOf<Item>()
        itemsRep = arrayListOf<Item>()

        val db = database.reference.child("items")

        /*val id:String = db.push().key!!
        db.child(id).setValue(Item("general","easy",id,"1","carbonara.jpg","\"Pasta Aglio e Olio Recipe:\n" +
                "1. Boil pasta in salted water until al dente.\n" +
                "2. In a pan, heat olive oil and sauté minced garlic until fragrant.\n" +
                "3. Add red pepper flakes and chopped parsley to the pan.\n" +
                "4. Toss the cooked pasta into the garlic oil mixture.\n" +
                "5. Add a splash of pasta water to create a sauce.\n" +
                "6. Season with salt and pepper, and top with grated Parmesan cheese.\n" +
                "7. Serve hot and enjoy your delicious Pasta Aglio e Olio!\"\n"+
                "Enjoy your meal!",1,"1", 0.toFloat(),0))*/

        itemsList.layoutManager = LinearLayoutManager(this)
        adapter = ItemsAdapter(items,this)
        itemsList.adapter = adapter

        val itemListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(!itemsRep.isEmpty())
                    itemsRep.clear()
                loadItemList(dataSnapshot)
                items.clear()
                items.addAll(itemsRep)
                sortItems()
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Item failed, log a message
            }
        }
        db.addValueEventListener(itemListener)

        adapter.onItemClick = {
            val intent = Intent(this, ItemActivity::class.java)
            intent.putExtra("item", it)
            startActivity(intent)
        }

        val exitBtn: ImageButton = findViewById(R.id.exitBtn)
        exitBtn.setOnClickListener{
            auth.signOut()
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }
        val profileBtn: ImageButton = findViewById(R.id.profileBtn)
        profileBtn.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        val favouritesBtn: ImageButton = findViewById(R.id.favBtn)
        favouritesBtn.setOnClickListener{
            val intent = Intent(this, FavouritesActivity::class.java)
            startActivity(intent)
        }
        val filterBtn: ImageButton = findViewById(R.id.filterBtn)
        filterBtn.setOnClickListener{
            showFilterDialog(this) { wasApplyButtonClicked ->
                if (wasApplyButtonClicked) {
                    items.clear()
                    items.addAll(itemsRep)
                    sortItems()
                    adapter.notifyDataSetChanged()
                }
            }
        }

        val searchView: SearchView = findViewById(R.id.searchText)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                items.clear()
                filterList(p0)
                adapter.notifyDataSetChanged()
                return true
            }
        })
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun loadItemList(dataSnapshot: DataSnapshot) {
        if(dataSnapshot.exists()){
            for(i in dataSnapshot.children) {
                val item = i.getValue(Item::class.java)
                itemsRep.add(item!!)
            }
        }
    }
    @SuppressLint("MissingInflatedId")
    fun showFilterDialog(context: Context, onApplyButtonClicked: (Boolean) -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val dialog = builder.create()

        dialogView.findViewById<Button>(R.id.applyBtn).setOnClickListener {
            moneyDirectionUp = when (dialogView.findViewById<RadioButton>(R.id.moneyUp).isChecked) {
                true -> true
                false ->when (dialogView.findViewById<RadioButton>(R.id.moneyDown).isChecked) {
                    true -> false
                    false -> null
                }
            }
            difficuly = when (dialogView.findViewById<RadioButton>(R.id.diffEasy).isChecked) {
                true -> "easy"
                false -> when (dialogView.findViewById<RadioButton>(R.id.diffMedium).isChecked) {
                    true -> "medium"
                    false -> when (dialogView.findViewById<RadioButton>(R.id.diffHard).isChecked) {
                        true -> "hard"
                        false -> "all"
                    }
                }
            }
            ratingDirectionUp = when (dialogView.findViewById<RadioButton>(R.id.ratingUp).isChecked) {
                true -> true
                false ->when (dialogView.findViewById<RadioButton>(R.id.ratingDown).isChecked) {
                    true -> false
                    false -> null
                }
            }

            onApplyButtonClicked(true)
            dialog.dismiss()

        }
        dialogView.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            difficuly = "all"
            moneyDirectionUp = null
            ratingDirectionUp = null
            onApplyButtonClicked(false)
            dialog.dismiss()
        }

        dialog.show()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun sortItems(){
        if(ratingDirectionUp != null && ratingDirectionUp == true){
            items.sortBy{it.avgRating}
        }
        else if(ratingDirectionUp != null){
            items.sortBy{it.avgRating}
            items.reverse()
        }

        if(moneyDirectionUp!= null && moneyDirectionUp == true){
            items.sortBy{it.price}
        }
        else if(moneyDirectionUp!= null){
            items.sortBy{it.price}
            items.reverse()
        }
        if(!difficuly.equals("all")) {
            val filteredItems = items.filter { it.diff.equals(difficuly) }
            items.clear()
            items.addAll(filteredItems)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun filterList(query: String?){
        if (query != null){
            val filteredList = ArrayList<Item>()
            for(i in itemsRep){
                if(i.name?.lowercase(Locale.ROOT)?.contains(query) == true)
                    filteredList.add(i)

            }
            if(filteredList.isEmpty())
                Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show()
            else{
                items.addAll(filteredList)
            }
        }

    }
}