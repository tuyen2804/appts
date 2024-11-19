package com.example.testbackend.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.testbackend.R
import com.example.testbackend.databinding.FragmentProfileBinding
import com.example.testbackend.ui.activity.BackgroundStudentActivity


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding?=null
    private val binding get()=_binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentProfileBinding.inflate(inflater,container,false)
        binding.btnBackgroundStudent.setOnClickListener(){
            var intent=Intent(requireContext(),BackgroundStudentActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}