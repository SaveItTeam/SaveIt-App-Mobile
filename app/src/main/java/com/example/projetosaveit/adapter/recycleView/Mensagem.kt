package com.example.projetosaveit.adapter.recycleView

data class Mensagem (
    val texto: String,
    val tipo: TipoMensagem,
    val idEmpresa: Long
)

data class MensagemWS (
    val text: String,
    val enterpriseId: Long
)

enum class TipoMensagem {
    ENVIADA, RECEBIDA
}