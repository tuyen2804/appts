package com.example.testbackend.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.testbackend.R
import com.example.testbackend.databinding.ActivityBackgroundStudentBinding
import com.example.testbackend.databinding.ActivityTypeOfAdmissionBinding

class TypeOfAdmissionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTypeOfAdmissionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTypeOfAdmissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnTypeOfAdmission1.setOnClickListener(){
            val intent=Intent(this,AdmissionInfo1Activity::class.java)
            startActivity(intent)
        }
        binding.btnTypeOfAdmission2.setOnClickListener(){
            val intent=Intent(this,AdmissionInfo2Activity::class.java)
            startActivity(intent)
        }
        binding.btnTypeOfAdmission3.setOnClickListener(){
            val intent=Intent(this,AdmissionInfo3Activity::class.java)
            startActivity(intent)
        }
    }
}