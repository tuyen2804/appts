package com.example.testbackend.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testbackend.R
import com.example.testbackend.model.Comment

class CommentAdapter(
    private val commentList: MutableList<Comment>,
    private val context: Context,
    private val onReplyClicked: (Comment) -> Unit  // Thêm callback để xử lý nhấn phản hồi
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.tvUserName)
        val time: TextView = view.findViewById(R.id.tvCommentTime)
        val commentContent: TextView = view.findViewById(R.id.tvCommentContent)
        val btnComment: TextView = view.findViewById(R.id.btnComment)
        val rvSubComment: RecyclerView = view.findViewById(R.id.rvSubComments)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_qanda, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]
        holder.userName.text = comment.userName
        holder.time.text = comment.time
        holder.commentContent.text = comment.content

        // Hiển thị nút phản hồi và xử lý khi nhấn vào
        holder.btnComment.setOnClickListener {
            onReplyClicked(comment)  // Thông báo cho activity khi nhấn phản hồi
        }

        // Hiển thị bình luận con nếu có
        if (comment.subComments.isNotEmpty()) {
            holder.rvSubComment.visibility = View.VISIBLE
            holder.rvSubComment.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.rvSubComment.adapter = CommentAdapter(comment.subComments, context, onReplyClicked)
        } else {
            holder.rvSubComment.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = commentList.size
}
