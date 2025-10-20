package com.example.projetosaveit.api.repository

import com.example.projetosaveit.api.network.RetrofitClientSql

class VitrineRepository {
    fun getVitrine(showcaseId : Long) = RetrofitClientSql.instance.getVitrine(showcaseId)

    fun getVitrineProdutos(category : String) = RetrofitClientSql.instance.getVitrineProdutos(category)
}