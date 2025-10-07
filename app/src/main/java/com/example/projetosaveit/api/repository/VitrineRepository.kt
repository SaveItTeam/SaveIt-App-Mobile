package com.example.projetosaveit.api.repository

class VitrineRepository {
    fun getVitrine(showcaseId : Long) = RetrofitClient.instance.getVitrine(showcaseId)

    fun getVitrineProdutos(category : String) = RetrofitClient.instance.getVitrineProdutos(category)
}