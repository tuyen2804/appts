package com.example.testbackend.model

data class Comment(
    val id: Long?, // ID của bình luận, nullable vì nó sẽ do backend sinh ra
    val userName: String,
    val time: String,
    val content: String,
    val parentId: Long?, // ID của bình luận cha, nullable nếu không có cha
    var subComments: MutableList<Comment> = mutableListOf() // Bình luận con (nested)
)

