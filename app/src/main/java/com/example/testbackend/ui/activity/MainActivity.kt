package com.example.testbackend.ui.activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testbackend.databinding.ActivityMainBinding
import com.example.testbackend.model.AuthResponse
import com.example.testbackend.model.User
import com.example.testbackend.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            if (isNetworkAvailable()) {
                val email = binding.username.text.toString()
                val password = binding.password.text.toString()

                val user = User(email, password)
                RetrofitInstance.api.login(user).enqueue(object : Callback<AuthResponse> {
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

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
