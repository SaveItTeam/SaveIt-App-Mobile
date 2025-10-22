package com.example.projetosaveit.api.repository

import com.example.projetosaveit.api.network.ApiService
import com.example.projetosaveit.api.network.RetrofitClientSql
import com.example.projetosaveit.model.EstoqueDTO
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call

class EstoqueRepository {
    fun postEstoque(estoque : EstoqueDTO) : Call<ResponseBody> {
        return RetrofitClientSql.instance.postEstoque(estoque)
    }
}