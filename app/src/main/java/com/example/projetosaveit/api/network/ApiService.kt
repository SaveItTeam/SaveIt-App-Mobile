package com.example.projetosaveit.api.network

import com.example.projetosaveit.adapter.recycleView.Produto
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("produto/selecionar")
    fun getProdutos(): Call<List<Produto>>
}