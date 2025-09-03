package com.example.projetosaveit.api.network

import com.example.projetosaveit.adapter.recycleView.Produto
import com.example.projetosaveit.model.VitrineDTO
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("produto/selecionar")
    fun getProdutos(): Call<List<Produto>>

    @GET("vitrine/selecionar")
    fun getVitrine(): Call<List<VitrineDTO>>
}