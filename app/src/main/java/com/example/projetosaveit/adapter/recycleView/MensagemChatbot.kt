package com.example.projetosaveit.adapter.recycleView

data class MensagemChatbot(
    val texto: String,
    val tipo: TipoMensagemChatbot
)

enum class TipoMensagemChatbot {
    USUARIO,
    BOT
}