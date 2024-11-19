package com.example.testbackend.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.testbackend.R
import com.example.testbackend.adapter.TsaAdapter
import com.example.testbackend.databinding.ActivityBackgroundStudentBinding
import com.example.testbackend.databinding.ActivityTsaTestBinding
import com.example.testbackend.model.TestItem

class TsaTestActivity : AppCompatActivity() {
    private lateinit var binding:ActivityTsaTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTsaTestBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Tạo danh sách dữ liệu mẫu
        val testList = listOf(
            TestItem(
                testName = "1",
                testLocation = "Hà Nội",
                testDate = "01/12/2024",
                registrationFee = "500,000 VND"
            ),
            TestItem(
                testName = "2",
                testLocation = "TP. Hồ Chí Minh",
                testDate = "05/12/2024",
                registrationFee = "600,000 VND"
            )
        )

        binding.rvTsa.adapter = TsaAdapter(testList)
    }
}
