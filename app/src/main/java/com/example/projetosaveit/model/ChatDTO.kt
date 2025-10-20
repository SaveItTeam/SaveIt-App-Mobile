package com.example.projetosaveit.model

data class ChatDTO (
    val id: String,
    val chatId: Long,
    val senderId: Long,
    var text: String,
    val sentAt: String,
    val read: Boolean,
    var enterpriseId: Long,
    var empresa: EmpresaDTO? = null
)