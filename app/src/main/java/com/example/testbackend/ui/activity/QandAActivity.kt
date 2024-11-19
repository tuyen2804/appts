package com.example.testbackend.ui.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testbackend.adapter.CommentAdapter
import com.example.testbackend.databinding.ActivityQandaBinding
import com.example.testbackend.model.Comment
import com.example.testbackend.network.ApiService
import com.example.testbackend.network.RetrofitInstance
import com.example.testbackend.service.WebSocketService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QandAActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQandaBinding
    private lateinit var commentAdapter: CommentAdapter
    private val commentList = mutableListOf<Comment>()

    // Biến để theo dõi nếu người dùng đang trả lời một bình luận cụ thể
    private var replyToComment: Comment? = null

    // Sử dụng RetrofitInstance để lấy apiService
    private val apiService = RetrofitInstance.api

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQandaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Gán LayoutManager và Adapter cho RecyclerView
        binding.rvComments.layoutManager = LinearLayoutManager(this)
        commentAdapter = CommentAdapter(commentList, this, ::onReplyClicked)
        binding.rvComments.adapter = commentAdapter

        // Khởi động WebSocketService
        val intent = Intent(this, WebSocketService::class.java)
        startService(intent)

        // Đăng ký lắng nghe sự kiện mới từ WebSocket
        LocalBroadcastManager.getInstance(this).registerReceiver(newCommentReceiver, IntentFilter("com.example.NEW_COMMENT"))

        // Tải bình luận từ API
        loadComments()

        // Xử lý sự kiện khi người dùng gửi bình luận
        binding.btnSendComment.setOnClickListener {
            val newCommentText = binding.etComment.text.toString()
            if (newCommentText.isNotEmpty()) {
                Log.d("Comment", replyToComment.toString())
                if (replyToComment == null) {
                    // Tạo một bình luận chính mới (không có parentId)
                    val newComment = Comment(
                        id = null,  // ID sẽ do backend tạo
                        userName = "User A",
                        time = "Just now",
                        content = newCommentText,
                        parentId = null // Bình luận chính không có parentId
                    )
                    // Gửi bình luận mới qua API
                    postComment(newComment)
                } else {
                    // Tạo một phản hồi cho bình luận đã chọn (có parentId)
                    val newSubComment = Comment(
                        id = null, // ID sẽ do backend tạo
                        userName = "User A",
                        time = "Just now",
                        content = newCommentText,
                        parentId = replyToComment?.id // Gửi parentId là id của bình luận cha
                    )
                    // Gửi bình luận con qua API
                    postComment(newSubComment)
                }

                binding.etComment.text.clear()  // Xóa nội dung nhập sau khi gửi
                cancelReply()  // Đặt lại trạng thái trả lời
            }
        }

        // Hủy trả lời nếu người dùng không muốn trả lời nữa
        binding.txtCancelReply.setOnClickListener {
            cancelReply()
        }
    }

    // Hàm giả lập để tải bình luận mẫu từ API
    @SuppressLint("NotifyDataSetChanged")
    private fun loadComments() {
        apiService.getComments().enqueue(object : Callback<List<Comment>> {
            override fun onResponse(call: Call<List<Comment>>, response: Response<List<Comment>>) {
                if (response.isSuccessful) {
                    commentList.clear()
                    response.body()?.let { commentList.addAll(it) }
                    commentAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                println("Error loading comments: ${t.message}")
            }
        })
    }

    // Gửi bình luận qua API
    private fun postComment(comment: Comment) {
        apiService.postComment(comment).enqueue(object : Callback<Comment> {
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                if (response.isSuccessful) {
                    // Không cập nhật giao diện ngay lập tức, đợi thông báo từ WebSocket
                    println("Bình luận đã gửi thành công, chờ thông báo từ WebSocket.")
                }
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                println("Lỗi khi đăng bình luận: ${t.message}")
            }
        })
    }

    // BroadcastReceiver để nhận dữ liệu từ WebSocket
    private val newCommentReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.getStringExtra("comment")
            message?.let {
                // Khi nhận được bình luận mới từ WebSocket, cập nhật giao diện
                val newComment = Gson().fromJson(it, Comment::class.java) // Giả sử parseCommentFromMessage là hàm chuyển chuỗi JSON thành đối tượng Comment
                Log.d("WebSocket", "New comment received: $newComment")
                // Nếu bình luận là bình luận chính
                if (newComment.parentId == null) {
                    commentList.add(0, newComment)
                    commentAdapter.notifyItemInserted(0)
                } else {
                    // Nếu là bình luận con, thêm vào subComments của bình luận cha
                    val parentComment = commentList.find { parent -> parent.id == newComment.parentId }
                    parentComment?.subComments?.add(newComment)
                    val parentIndex = commentList.indexOf(parentComment)
                    commentAdapter.notifyItemChanged(parentIndex)
                }

                // Cuộn đến bình luận mới nhất
                binding.rvComments.scrollToPosition(0)
            }
        }
    }

    // Hàm callback khi nhấn nút phản hồi
    private fun onReplyClicked(comment: Comment) {
        replyToComment = comment
        Log.d("ReplyToComment", "Replying to comment ID: ${replyToComment?.id}")
        binding.replyLayout.visibility = View.VISIBLE
        binding.txtUser.text = comment.userName
    }


    // Hủy trạng thái trả lời
    private fun cancelReply() {
        replyToComment = null
        binding.replyLayout.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        // Hủy đăng ký receiver khi activity bị destroy
        LocalBroadcastManager.getInstance(this).unregisterReceiver(newCommentReceiver)
    }
}
