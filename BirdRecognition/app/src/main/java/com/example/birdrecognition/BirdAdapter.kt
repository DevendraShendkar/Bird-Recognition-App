package com.example.birdrecognition

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class BirdAdapter(private val context: Context, private val birdList: List<Bird>) :
    RecyclerView.Adapter<BirdAdapter.BirdViewHolder>() {

    class BirdViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val storage = Firebase.storage
        private val storageRef = storage.reference

        val themesImg: ImageView = view.findViewById(R.id.themeImg)
        val themeName: TextView = view.findViewById(R.id.themeName)

        fun bind(bird: Bird) {
            // Process the bird name
            val processedName = bird.birdName
                ?.replace("_", " ") // Replace underscores with spaces
                ?.replace(Regex("[0-9]"), "") // Remove all numbers
                ?.removeSuffix(".jpg") // Remove ".jpg" extension

            themeName.text = processedName

            val imagePath = "birds/${bird.birdName}" // Use the user-given name to get the image
            val imageRef = storageRef.child(imagePath)

            // Get the download URL of the image
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(itemView)
                    .load(uri)
                    .placeholder(R.drawable.placeholder) // Replace with a placeholder drawable
                    .error(R.drawable.placeholder)
                    .into(themesImg)
            }.addOnFailureListener { exception ->

                Log.e("BirdRecognition", "Failed to get download URL: ${exception.message}")
                // Load a placeholder or clear the image view on failure
                Glide.with(itemView)
                    .clear(themesImg)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirdViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.themes_item, parent, false)
        return BirdViewHolder(view)
    }

    override fun getItemCount(): Int {
        return birdList.size
    }

    override fun onBindViewHolder(holder: BirdViewHolder, position: Int) {
        holder.bind(birdList[position])
    }
}

