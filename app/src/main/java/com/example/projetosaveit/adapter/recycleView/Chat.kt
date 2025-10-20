package com.example.projetosaveit.adapter.recycleView

data class Chat (
    val id: String,
    val chatId: Long,
    val senderId: Long,
    val text: String,
    val sentAt: String,
    val read: Boolean,
    val enterpriseId: Long
)