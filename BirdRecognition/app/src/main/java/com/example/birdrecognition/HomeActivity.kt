package com.example.birdrecognition

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import com.example.birdrecognition.databinding.ActivityHomeBinding
import androidx.fragment.app.Fragment


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var textToSpeech: TextToSpeech
    private val imageSize = 32
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            val currentFragment = supportFragmentManager.findFragmentById(R.id.container)

            if (menuItem.itemId == binding.bottomNav.selectedItemId || menuItem.itemId == currentFragment?.id) {
                return@setOnItemSelectedListener true
            }
            when (menuItem.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment())

                }

                R.id.search -> {
                    replaceFragment(SearchFragment())

                }

                else -> {}
            }
            true
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.toolbar_menu, menu)
//        return true
//    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()

    }

}

