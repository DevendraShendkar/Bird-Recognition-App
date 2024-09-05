package com.example.birdrecognition

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.birdrecognition.databinding.FragmentSearchBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private val birdList = mutableListOf<Bird>()
    private lateinit var adapter: BirdAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        setupRecyclerView()
        setupSearchBar()
        return binding.root
    }

    private fun setupRecyclerView() {

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = BirdAdapter(requireContext(), birdList)
        binding.recyclerView.adapter = adapter
    }

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val birdName = s.toString().trim().replace(" ", "_")
                if (birdName.isNotEmpty()) {
                    reloadImages(birdName)
                } else {
                    birdList.clear()

                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun reloadImages(birdName: String) {
        val imagePath = "birds"
        val folderRef = storageRef.child(imagePath)


        folderRef.listAll().addOnSuccessListener { listResult ->

            val items = listResult.items
            if (items.isNotEmpty()) {
                birdList.clear()

                val filteredItems = items.filter { item ->
                    item.name.startsWith(birdName, ignoreCase = true)
                }

                for (item in filteredItems) {
                    val bird = Bird(item.name)
                    birdList.add(bird)
                }
                adapter.notifyDataSetChanged()
            } else {

                birdList.clear()
                adapter.notifyDataSetChanged()

            }
        }.addOnFailureListener {

            birdList.clear()
            adapter.notifyDataSetChanged()
        }
    }


}
