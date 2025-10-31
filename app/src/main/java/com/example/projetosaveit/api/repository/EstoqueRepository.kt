package com.example.projetosaveit.api.repository

import com.example.projetosaveit.api.network.ApiService
import com.example.projetosaveit.api.network.RetrofitClientSql
import com.example.projetosaveit.model.EstoqueDTO
import com.example.projetosaveit.model.EstoqueInsertDTO
import com.example.projetosaveit.model.RelatorioDTO
import com.example.projetosaveit.model.RelatorioProdutoDTO
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call

class EstoqueRepository {

    fun getRelatorioMensal(enterpriseId: Long): Call<List<RelatorioDTO>> {
        return RetrofitClientSql.instance.getRelatorioProdutos(enterpriseId)
    }

    fun postEstoque(estoque: EstoqueDTO) : Call<ResponseBody> {
        return RetrofitClientSql.instance.postEstoque(estoque)
    }

    fun getRelatorioProduto(enterpriseId : Long, productId : Long) : Call<List<RelatorioProdutoDTO>> {
        return RetrofitClientSql.instance.getRelatorioProduto(enterpriseId, productId)
    }
}