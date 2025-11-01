package com.example.projetosaveit.api.repository

import com.example.projetosaveit.api.network.ChatBotClient
import com.example.projetosaveit.model.ChatRequest
import com.example.projetosaveit.model.ChatResponse
import com.example.projetosaveit.model.HistoricoResponse
import com.example.projetosaveit.model.HistoricoSessaoRequest
import com.example.projetosaveit.model.IniciarChatRequest
import com.example.projetosaveit.model.IniciarChatResponse
import retrofit2.Call
import retrofit2.http.Body

class ChatbotRepository {
    fun iniciarChat(request: IniciarChatRequest): Call<IniciarChatResponse> {
        return ChatBotClient.instance.iniciarChat(request)
    }

    fun enviarMensagem(request: ChatRequest): Call<ChatResponse> {
        return ChatBotClient.instance.enviarMensagem(request)
    }

    fun obterHistoricoFuncionario(funcionarioId: Int): Call<HistoricoResponse> {
        val request = mapOf("funcionario_id" to funcionarioId)
        return ChatBotClient.instance.obterHistoricoFuncionario(request)
    }

    // Histórico de mensagens de uma sessão específica
    fun obterHistoricoSessao(request: HistoricoSessaoRequest): Call<HistoricoResponse> {
        return ChatBotClient.instance.obterHistoricoSessao(request)
    }
}