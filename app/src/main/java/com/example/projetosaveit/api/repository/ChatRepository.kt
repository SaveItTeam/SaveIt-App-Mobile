package com.example.projetosaveit.api.repository

import com.example.projetosaveit.api.network.RetrofitClientMongo
import com.example.projetosaveit.api.network.RetrofitClientSql
import com.example.projetosaveit.model.ChatDTO
import retrofit2.Call

class ChatRepository {
    fun getChatPorEnterprise(idEmpresa: Long): Call<List<ChatDTO>> {
        return RetrofitClientMongo.instance.getChatsEmpresa(idEmpresa)
    }

    fun getChatUltimaMensagem(idChat: Long, idEmpresa: Long): Call<ChatDTO> {
        return RetrofitClientMongo.instance.getChatUltimaMensagem(idChat, idEmpresa)
    }

    fun getChatOutraEmpresa(idChat: Long, idEmpresa: Long): Call<ChatDTO> {
        return RetrofitClientMongo.instance.getChatOutraEmpresa(idChat, idEmpresa)
    }

    fun getChatHistorico(idChat: Long): Call<List<ChatDTO>> {
        return RetrofitClientMongo.instance.getChatsHistorico(idChat)
    }

    fun marcarComoLida(chatId: Long): Call<Void> {
        return RetrofitClientSql.instance.marcarComoLida(chatId)
    }
}