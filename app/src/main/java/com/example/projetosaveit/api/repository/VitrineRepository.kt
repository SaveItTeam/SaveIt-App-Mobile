package com.example.projetosaveit.api.repository

import com.example.projetosaveit.api.network.RetrofitClientSql
import com.example.projetosaveit.model.VitrineDTO
import com.example.projetosaveit.model.VitrineInsertDTO

class VitrineRepository {
    fun getVitrine(showcaseId : Long) = RetrofitClientSql.instance.getVitrine(showcaseId)

    fun getVitrineProdutos(category : String) = RetrofitClientSql.instance.getVitrineProdutos(category)

    fun getVitrineEmpresa(enterpriseId : Long) = RetrofitClientSql.instance.getVitrinesEmpresa(enterpriseId)

    fun postVitrine(showcase : VitrineInsertDTO) = RetrofitClientSql.instance.postVitrine(showcase)

    fun getVitrineNovas(ultimoCheck: String) = RetrofitClientSql.instance.getVitrinesNovas(ultimoCheck)
}