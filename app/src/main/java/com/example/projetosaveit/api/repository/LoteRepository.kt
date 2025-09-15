package com.example.projetosaveit.api.repository

import com.example.projetosaveit.adapter.recycleView.Produto
import com.example.projetosaveit.model.LoteInsertDTO
import com.example.projetosaveit.model.ProdutoDTO
import okhttp3.ResponseBody
import retrofit2.Call

class LoteRepository {
    fun postLote(loteInsert : LoteInsertDTO): Call<ResponseBody> {
        return RetrofitClient.instance.postLote(loteInsert);
    }

    fun getLoteProduto(idEmpresa : Long): Call<List<Produto>> {
        return RetrofitClient.instance.getProdutos(idEmpresa)
    }
}