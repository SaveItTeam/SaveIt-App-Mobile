package com.example.projetosaveit.model

data class IniciarChatRequest(
    val empresa_id: Int,
    val funcionario_id: Int
)

data class IniciarChatResponse(
    val session_id: String
)

data class ChatRequest(
    val user_input: String,
    val empresa_id: Int,
    val funcionario_id: Int,
    val session_id: String
)

data class ChatResponse(
    val resposta_assistente: String
)

data class HistoricoSessaoRequest(
    val funcionario_id: Int,
    val session_id: String
)

data class HistoricoResponse(
    val historico: List<MensagemHistorico> = emptyList(),
    val sessoes: List<SessaoHistorico> = emptyList()
)

data class MensagemHistorico(
    val role: String,
    val content: String
)

data class SessaoHistorico(
    val session_id: String,
    val data: String? = null
)