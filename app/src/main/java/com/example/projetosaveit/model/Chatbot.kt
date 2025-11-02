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

data class HistoricoSessaoResponse(
    val status: String,
    val funcionario_id: Int,
    val session_id: String,
    val historico: List<MensagemHistorico>
)

data class HistoricoFuncionarioResponse(
    val status: String,
    val funcionario_id: Int,
    val total_sessoes: Int,
    val historicos: List<MensagemHistoricoCompleto>
)

data class MensagemHistorico(
    val tipo: String,
    val mensagem: String
)

data class MensagemHistoricoCompleto(
    val empresa_id: Int,
    val funcionario_id: Int,
    val session_id: String,
    val role: String,
    val content: String,
    val timestamp: String
)

data class HistoricoResponse(
    val historico: List<MensagemChatHistorico> = emptyList(),
    val sessoes: List<SessaoHistorico> = emptyList()
)

data class MensagemChatHistorico(
    val role: String,
    val content: String,
    val timestamp: String? = null
)

data class SessaoHistorico(
    val session_id: String,
    val data: String? = null
)