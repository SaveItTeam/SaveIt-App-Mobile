package com.example.projetosaveit.api.repository

import com.example.projetosaveit.api.network.ApiService
import com.example.projetosaveit.api.network.RetrofitClientSql
import com.example.projetosaveit.model.EstoqueDTO
import com.example.projetosaveit.model.EstoqueInsertDTO
import com.example.projetosaveit.model.RelatorioDTO
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call

class EstoqueRepository {

    fun getRelatorioMensal(enterpriseId: Long): Call<List<RelatorioDTO>> {
        return RetrofitClientSql.instance.getRelatorioProdutos(enterpriseId)
    }

    fun postEstoque(estoque: EstoqueInsertDTO) : Call<ResponseBody> {
        return RetrofitClientSql.instance.postEstoque(estoque)
    }
}