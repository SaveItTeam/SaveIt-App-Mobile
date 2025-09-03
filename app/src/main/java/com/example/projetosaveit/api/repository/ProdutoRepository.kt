package com.example.projetosaveit.api.repository

import com.example.projetosaveit.api.network.ApiService
import com.example.projetosaveit.model.ProdutoDTO
import retrofit2.Call


class ProdutoRepository {
    fun getProdutos(): Call<List<ProdutoDTO>> {
        return RetrofitClient.instance.getProdutos()
    }
}