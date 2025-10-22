package com.example.projetosaveit.api.repository

import android.media.Image
import com.example.projetosaveit.api.network.RetrofitClientSql
import com.example.projetosaveit.model.ImagemDTO
import okhttp3.ResponseBody
import retrofit2.Call

class ImagemRepository {
    fun getImagemPorProduto(idProduto: Long) : Call<ImagemDTO> {
        return RetrofitClientSql.instance.getImagemProduto(idProduto)
    }

}