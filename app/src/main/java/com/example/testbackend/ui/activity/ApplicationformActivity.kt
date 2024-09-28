package com.example.testbackend.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.testbackend.ui.fragment.CitizenIdentificationFragment
import com.example.testbackend.R
import com.example.testbackend.databinding.ActivityApplicationformBinding

class ApplicationformActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplicationformBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplicationformBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Display CitizenIdentificationFragment by default
        replaceFragment(CitizenIdentificationFragment())
    }

    fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
