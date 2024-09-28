package com.example.testbackend.ui.activity

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.testbackend.ui.fragment.HomeFragment
import com.example.testbackend.ui.fragment.NewsFragment
import com.example.testbackend.ui.fragment.ProfileFragment
import com.example.testbackend.R

class HomeActivity : AppCompatActivity() {
    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Khởi tạo FragmentManager
        fragmentManager = supportFragmentManager

        // Thiết lập sự kiện click cho các nút trong BottomAppBar
        val btnHome = findViewById<LinearLayout>(R.id.btnHome)
        val btnProfile = findViewById<LinearLayout>(R.id.btnProfile)
        val btnNews = findViewById<LinearLayout>(R.id.btnNews)

        btnHome.setOnClickListener {
            replaceFragment(HomeFragment())
        }

        btnProfile.setOnClickListener {
            replaceFragment(ProfileFragment())
        }
        btnNews.setOnClickListener {
            replaceFragment(NewsFragment())
        }

        // Hiển thị HomeFragment khi Activity khởi tạo
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .replace(R.id.home, fragment)
            .commit()
    }
}
