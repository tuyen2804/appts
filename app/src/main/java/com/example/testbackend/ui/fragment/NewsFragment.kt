package com.example.testbackend.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.testbackend.databinding.FragmentNewsBinding

class NewsFragment : Fragment() {
    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment with ViewBinding
        _binding = FragmentNewsBinding.inflate(inflater, container, false)

        // Sử dụng binding thay vì findViewById()
        val myWebView: WebView = binding.webview

        // Thiết lập WebViewClient để load trang web bên trong ứng dụng
        myWebView.webViewClient = WebViewClient()

        // Cấu hình WebView, bật JavaScript nếu cần
        val webSettings: WebSettings = myWebView.settings
        webSettings.javaScriptEnabled = true

        // Load URL
        myWebView.loadUrl("https://ts.hust.edu.vn/b/dai-hoc")

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}