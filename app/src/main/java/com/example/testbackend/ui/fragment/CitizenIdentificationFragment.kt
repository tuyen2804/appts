package com.example.testbackend.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.testbackend.databinding.FragmentCitizenIdentificationBinding
import com.example.testbackend.helper.DatabaseHelper
import com.example.testbackend.helper.DatePickerHelper
import com.example.testbackend.helper.ImagePickerHelper
import com.example.testbackend.helper.PopupMenuHelper
import com.example.testbackend.model.Student
import com.example.testbackend.network.RetrofitInstance
import com.example.testbackend.ui.activity.ApplicationformActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CitizenIdentificationFragment : Fragment() {

    private var _binding: FragmentCitizenIdentificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var imagePickerHelper: ImagePickerHelper
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var popupMenuHelper: PopupMenuHelper
    private lateinit var datePickerHelper: DatePickerHelper

    private var imgStudent: MultipartBody.Part? = null
    private var imgFrontCard: MultipartBody.Part? = null
    private var imgBackCard: MultipartBody.Part? = null

    private var urlStudent: String? = null
    private var urlFrontCard: String? = null
    private var urlBackCard: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCitizenIdentificationBinding.inflate(inflater, container, false)

        // Khởi tạo các helper
        imagePickerHelper = ImagePickerHelper(requireActivity() as ApplicationformActivity)
        databaseHelper = DatabaseHelper(requireContext())
        popupMenuHelper = PopupMenuHelper(requireContext())
        datePickerHelper = DatePickerHelper(requireContext())

        setupListeners()  // Thiết lập các sự kiện khi người dùng tương tác

        return binding.root
    }

    private fun setupListeners() {
        // Lấy ảnh từ thư viện khi người dùng nhấn vào ImageView
        binding.imgStudent.setOnClickListener {
            imagePickerHelper.launchImagePickerAndSetImage(binding.imgStudent) { uri ->
                imgStudent = imagePickerHelper.getMultipartImage(requireContext().contentResolver, uri, "photo")
            }
        }

        binding.imgFrontCard.setOnClickListener {
            imagePickerHelper.launchImagePickerAndSetImage(binding.imgFrontCard) { uri ->
                imgFrontCard = imagePickerHelper.getMultipartImage(requireContext().contentResolver, uri, "idFront")
            }
        }

        binding.imgBackCard.setOnClickListener {
            imagePickerHelper.launchImagePickerAndSetImage(binding.imgBackCard) { uri ->
                imgBackCard = imagePickerHelper.getMultipartImage(requireContext().contentResolver, uri, "idBack")
            }
        }


        // Các popup menu và date picker
        binding.menuGender.setOnClickListener {
            val genders = listOf("Nam", "Nữ", "Khác")
            popupMenuHelper.showPopupMenu(it, genders, binding.txtGender)
        }

        binding.menuCity.setOnClickListener {
            val cities = databaseHelper.getCities()
            popupMenuHelper.showPopupMenu(it, cities, binding.txtCity)
        }

        binding.menuDistrict.setOnClickListener {
            val districts = databaseHelper.getDistricts(binding.txtCity.text.toString())
            popupMenuHelper.showPopupMenu(it, districts, binding.txtDistrict)
        }

        binding.menuWard.setOnClickListener {
            val wards = databaseHelper.getWards(binding.txtDistrict.text.toString())
            popupMenuHelper.showPopupMenu(it, wards, binding.txtWard)
        }

        binding.menuDate.setOnClickListener {
            datePickerHelper.showDatePickerDialog(binding.txtDateIssue)
        }
        binding.menuIssuePlace.setOnClickListener(){
            val cities = databaseHelper.getCities()
            popupMenuHelper.showPopupMenu(it, cities,binding.txtIssuePlace)
        }

        binding.menuDatebirth.setOnClickListener {
            datePickerHelper.showDatePickerDialog(binding.txtDatebirth)
        }

        binding.menuEthnicity.setOnClickListener {
            val ethnicities = listOf("Kinh", "Tày", "Mường")
            popupMenuHelper.showPopupMenu(it, ethnicities, binding.txtEthnicity)
        }

        binding.menuNationality.setOnClickListener {
            val nationalities = listOf("Viet Nam", "UK", "Thai Lan")
            popupMenuHelper.showPopupMenu(it, nationalities, binding.txtNationality)
        }

        binding.menuReligion.setOnClickListener {
            val religions = listOf("Đạo", "Phật", "Không")
            popupMenuHelper.showPopupMenu(it, religions, binding.txtReligion)
        }

        // Khi nhấn "Tiếp tục", gửi thông tin sinh viên và ảnh
        binding.btnNext.setOnClickListener {
            Log.d("CitizenFragment", "Button Next clicked, starting uploadStudentData")
            uploadStudentData()
        }
    }

    private fun uploadStudentData() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs",
            Context.MODE_PRIVATE
        )
        val accountId = sharedPreferences.getString("account_id", "1") // Giá trị mặc định là "1" nếu không có

        // Tạo đối tượng Student
        val student = Student(
            accountId = accountId ?: "1",
            photoUrl = urlStudent ?: "",
            idCardFrontUrl = urlFrontCard ?: "",
            idCardBackUrl = urlBackCard ?: "",
            fullName = binding.txtName.text.toString(),
            gender = binding.txtGender.text.toString(),
            birthDate = binding.txtDatebirth.text.toString(),
            birthCity = binding.txtCity.text.toString(),
            birthDistrict = binding.txtDistrict.text.toString(),
            birthWard = binding.txtWard.text.toString(),
            idNumber = binding.txtCardNumber1.text.toString(),
            issueDate = binding.txtDateIssue.text.toString(),
            issuePlace = binding.txtIssuePlace.text.toString(),
            nationality = binding.txtNationality.text.toString(),
            ethnicity = binding.txtEthnicity.text.toString(),
            religion = binding.txtReligion.text.toString()
        )
        Log.d("uploadStudentData", "City: ${student.birthCity}")
        Log.d("uploadStudentData", "District: ${student.birthDistrict}")
        Log.d("uploadStudentData", "Ward: ${binding.txtWard.text.toString()}")

        // Chuyển đổi Student thành JSON
        val studentJson = Gson().toJson(student)
        val studentRequestBody = studentJson.toRequestBody("application/json".toMediaTypeOrNull())

        RetrofitInstance.api.uploadStudentData(studentRequestBody, imgStudent, imgFrontCard, imgBackCard)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        // Xử lý khi upload thành công
                    } else {
                        // Xử lý khi upload thất bại
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    // Xử lý khi có lỗi
                }
            })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}