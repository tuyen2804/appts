package com.example.testbackend.ui.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.testbackend.databinding.ActivityBackgroundStudentBinding
import com.example.testbackend.model.Student
import com.example.testbackend.network.RetrofitInstance
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BackgroundStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBackgroundStudentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackgroundStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreference = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val accountId = sharedPreference.getString("account_id", "1")
        val gson = Gson()
        // Đọc dữ liệu sinh viên từ cache
        val cachedStudentData = sharedPreference.getString("cached_student_data", null)
        if (cachedStudentData != null) {
            val student:Student?=gson.fromJson(cachedStudentData,Student::class.java)
            if (student != null) {
                updateStudent(student)
            }
        }


        // Khi người dùng nhấn nút "Update Background"
        binding.btnUpdateBackground.setOnClickListener {
            RetrofitInstance.api.getStudentData(accountId = accountId.toString()).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val studentjson = response.body()

                        val studentObj: Student? = studentjson?.let { gson.fromJson(it, Student::class.java) }
                        studentjson?.let {
                            // Lưu dữ liệu sinh viên vào cache
                            val studentDataString = it.toString() // Chuyển đổi JsonObject thành chuỗi JSON
                            sharedPreference.edit().putString("cached_student_data", studentDataString).apply()

                            // Hiển thị dữ liệu sinh viên lên giao diện (nếu cần thiết)
                            studentObj?.let { student ->
                                updateStudent(studentObj)
                            }
                        }
                    } else {
                        // Xử lý khi phản hồi không thành công
                        println("Failed to get student details: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    // Xử lý khi gọi API thất bại
                    println("Error: ${t.message}")
                }
            })
        }

    }
    fun updateStudent(student: Student){

        Glide.with(binding.imgStudent)
            .load(RetrofitInstance.url+student.photoUrl)
            .into(binding.imgStudent)
        Glide.with(binding.imgFrontCard)
            .load(RetrofitInstance.url+student.idCardFrontUrl)
            .into(binding.imgFrontCard)
        Glide.with(binding.imgBackCard)
            .load(RetrofitInstance.url+student.idCardBackUrl)
            .into(binding.imgBackCard)
        binding.txtName.text=student.fullName
        binding.txtGender.text=student.gender
        binding.txtBirthCity.text=student.birthCity
        binding.txtBirthDistrict.text=student.birthDistrict
        binding.txtBirthWard.text=student.birthWard
        binding.txtCardNumber.text=student.idNumber
        binding.txtIssuePlace.text=student.issuePlace
        binding.txtIssueDate.text=student.issueDate
        binding.txtReligion.text=student.religion
        binding.txtEthnicity.text=student.ethnicity
        binding.txtNationality.text=student.nationality
    }
}
