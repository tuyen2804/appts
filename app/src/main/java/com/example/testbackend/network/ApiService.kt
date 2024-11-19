package com.example.testbackend.network

import com.example.testbackend.model.AuthResponse
import com.example.testbackend.model.Comment
import com.example.testbackend.model.Event
import com.example.testbackend.model.Student
import com.example.testbackend.model.User
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("/api/accounts/login")
    fun login(@Body user: User): Call<AuthResponse>

    @GET("/api/events")
    fun getEvents(@Header("Authorization") authToken: String): Call<List<Event>>

    @POST("/api/files/uploadBase64")
    fun uploadBase64Image(
        @Body imageData: JsonObject
    ): Call<JsonObject>

    @POST("/api/add")
    fun addEvent(
        @Body event: Event,
        @Header("Authorization") authToken: String
    ): Call<Void>

    @Multipart
    @POST("/api/students/saveOrUpdate")
    fun uploadStudentData(
        @Part("student") studentJson: RequestBody,   // Thông tin sinh viên dưới dạng JSON
        @Part photo: MultipartBody.Part?,            // Ảnh thẻ sinh viên
        @Part idFront: MultipartBody.Part?,          // Ảnh mặt trước CMND/CCCD
        @Part idBack: MultipartBody.Part?            // Ảnh mặt sau CMND/CCCD
    ): Call<JsonObject>

    @GET("/api/students/getStudent")
    fun getStudentData(
        @Query ("accountId") accountId:String
    ): Call<JsonObject>
    @POST("/comments")
    fun postComment(@Body comment: Comment): Call<Comment>

    @GET("/api/comments")
    fun getComments(): Call<List<Comment>>
}