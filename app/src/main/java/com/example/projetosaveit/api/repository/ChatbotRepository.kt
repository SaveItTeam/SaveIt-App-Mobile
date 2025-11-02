package com.example.projetosaveit.api.repository

import com.example.projetosaveit.api.network.ChatBotClient
import com.example.projetosaveit.model.ChatRequest
import com.example.projetosaveit.model.ChatResponse
import com.example.projetosaveit.model.IniciarChatRequest
import com.example.projetosaveit.model.IniciarChatResponse
import retrofit2.Call

class ChatbotRepository {
    fun iniciarChat(request: IniciarChatRequest): Call<IniciarChatResponse> {
        return ChatBotClient.instance.iniciarChat(request)
    }

    fun enviarMensagem(request: ChatRequest): Call<ChatResponse> {
        return ChatBotClient.instance.enviarMensagem(request)
    }

    fun obterHistoricoFuncionario(funcionarioId: Int) =
        ChatBotClient.instance.obterHistoricoFuncionario(funcionarioId)

    fun obterHistoricoSessao(funcionarioId: Int, sessionId: String) =
        ChatBotClient.instance.obterHistoricoSessao(funcionarioId, sessionId)
}