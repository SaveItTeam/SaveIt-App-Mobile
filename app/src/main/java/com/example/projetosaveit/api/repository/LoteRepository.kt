package com.example.projetosaveit.api.repository

import com.example.projetosaveit.adapter.recycleView.Produto
import com.example.projetosaveit.api.network.RetrofitClientSql
import com.example.projetosaveit.model.LoteDTO
import com.example.projetosaveit.model.LoteInsertDTO
import com.example.projetosaveit.model.ProdutoDTO
import com.example.projetosaveit.model.ProdutoInfoDTO
import okhttp3.ResponseBody
import retrofit2.Call

class LoteRepository {
    fun postLote(loteInsert : LoteInsertDTO): Call<ResponseBody> {
        return RetrofitClientSql.instance.postLote(loteInsert);
    }

    fun getLoteProduto(idEmpresa : Long): Call<List<Produto>> {
        return RetrofitClientSql.instance.getProdutos(idEmpresa)
    }

    fun getProdutoId(idProduto : Long): Call<ProdutoInfoDTO> {
        return RetrofitClientSql.instance.getProdutoId(idProduto)
    }

    fun patchProdutoId(id : Long, updates : Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody> {
        return RetrofitClientSql.instance.patchProdutoId(id, updates)
    }

    fun getLoteSku(sku: String) : Call<LoteDTO> {
        return RetrofitClientSql.instance.getBatchSku(sku)
    }
}