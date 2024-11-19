package com.example.testbackend.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.testbackend.databinding.ActivityMainBinding
import com.example.testbackend.model.AuthResponse
import com.example.testbackend.model.User
import com.example.testbackend.network.RetrofitInstance
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Kiểm tra và yêu cầu quyền POST_NOTIFICATIONS nếu đang chạy trên Android 13 trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Quyền đã được cấp, lấy FCM token
                fetchFCMToken()
            }
        } else {
            // Không cần yêu cầu quyền nếu là Android 12 trở xuống
            fetchFCMToken()
        }

        binding.btnLogin.setOnClickListener {
            if (isNetworkAvailable()) {
                val email = binding.username.text.toString()
                val password = binding.password.text.toString()

                val user = User(email, password)
                RetrofitInstance.api.login(user).enqueue(object : Callback<AuthResponse> {
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                        if (response.isSuccessful) {
                            val authResponse = response.body()
                            if (authResponse?.auth == true) {
                                // Lưu token, role, và account_id vào SharedPreferences
                                val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                with(sharedPreferences.edit()) {
                                    putString("access_token", authResponse.accessToken)
                                    putString("refresh_token",authResponse.refreshToken)
                                    putString("role", authResponse.role)
                                    putString("account_id", authResponse.account_id)
                                    apply()
                                }

                                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                                intent.putExtra("username", email)
                                startActivity(intent)
                            } else {
                                binding.log.text = "Sai thông tin"
                                Log.d("LoginActivity", "Auth failed: ${authResponse?.auth}")
                            }
                        } else {
                            binding.log.text = "Lỗi: ${response.message()}"
                            Log.d("LoginActivity", "Response error: ${response.message()}")
                        }
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                        binding.log.text = "Network error: ${t.message}"
                        Log.d("LoginActivity", "Network error", t)
                        Toast.makeText(this@MainActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    // Trình xử lý để yêu cầu quyền POST_NOTIFICATIONS
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("FCM Token", "Notification permission granted.")
            // Lấy FCM token sau khi được cấp quyền
            fetchFCMToken()
        } else {
            Log.w("FCM Token", "Notification permission denied.")
        }
    }

    // Lấy token FCM khi đã có quyền POST_NOTIFICATIONS
    private fun fetchFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM Token", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Lấy token thành công
            val token = task.result

            // Log token
            Log.d("FCM Token", "Token: $token")
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
