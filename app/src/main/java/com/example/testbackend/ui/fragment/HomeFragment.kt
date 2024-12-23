package com.example.testbackend.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.testbackend.databinding.FragmentHomeBinding
import com.example.testbackend.ui.activity.ApplicationformActivity
import com.example.testbackend.ui.activity.QandAActivity
import com.example.testbackend.ui.activity.TsaTestActivity
import com.example.testbackend.ui.activity.TypeOfAdmissionActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout với ViewBinding cho HomeFragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Cài đặt RecyclerView
        eventAdapter = EventAdapter(emptyList())

        binding.btnRegisterStudent.setOnClickListener {
            val intent= Intent(requireContext(), ApplicationformActivity::class.java)
            startActivity(intent)
        }
        binding.btnXTTN.setOnClickListener(){
            val intent= Intent(requireContext(), TypeOfAdmissionActivity::class.java)
            startActivity(intent)
        }
        binding.btnTSA.setOnClickListener(){
            val intent= Intent(requireContext(), TsaTestActivity::class.java)
            startActivity(intent)
        }

        // Xử lý nút "btnAdmission"
        binding.btnAdmission.setOnClickListener {
            val intent = Intent(requireContext(), ApplicationformActivity::class.java)
            startActivity(intent)
        }
        binding.btnQandA.setOnClickListener(){
            val intent = Intent(requireContext(), QandAActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}