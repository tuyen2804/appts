package com.example.testbackend.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.reactivex.android.schedulers.AndroidSchedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

class WebSocketService : Service() {

    private lateinit var stompClient: StompClient

    override fun onCreate() {
        super.onCreate()
        connectWebSocket()
    }

    // Kết nối WebSocket
    @SuppressLint("CheckResult")
    private fun connectWebSocket() {
        // Khởi tạo StompClient và kết nối WebSocket
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://192.168.0.7:8080/ws/websocket")

        // Lắng nghe các sự kiện của WebSocket (mở, đóng, lỗi, heartbeat)
        stompClient.lifecycle()
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { event ->
                when (event.type) {
                    LifecycleEvent.Type.OPENED -> {
                        println("WebSocket connected")
                    }
                    LifecycleEvent.Type.ERROR -> {
                        println("WebSocket error: ${event.exception}")
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        println("WebSocket closed")
                    }
                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                        println("WebSocket failed server heartbeat")
                    }
                    else -> {
                        println("Unknown WebSocket event: ${event.type}")
                    }
                }
            }

        // Đăng ký lắng nghe tin nhắn từ WebSocket trên topic "/topic/comments"
        stompClient.topic("/topic/comments").subscribe { topicMessage ->
            val message = topicMessage.payload
            broadcastMessage(message)  // Phát thông báo khi nhận được comment mới từ WebSocket
        }

        // Kết nối tới WebSocket
        stompClient.connect()
    }

    // Phát đi thông báo khi nhận được comment mới từ WebSocket
    private fun broadcastMessage(message: String) {
        val intent = Intent("com.example.NEW_COMMENT")
        intent.putExtra("comment", message)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent) // Sử dụng LocalBroadcastManager
    }

    override fun onDestroy() {
        super.onDestroy()
        stompClient.disconnect()  // Ngắt kết nối khi không cần nữa
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
