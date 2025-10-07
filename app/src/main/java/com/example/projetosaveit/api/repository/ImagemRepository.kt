package com.example.projetosaveit.api.repository

import android.media.Image
import com.example.projetosaveit.model.ImagemDTO
import okhttp3.ResponseBody
import retrofit2.Call

class ImagemRepository {
    fun getImagemPorProduto(idProduto: Long) : Call<ImagemDTO> {
        return RetrofitClient.instance.getImagemProduto(idProduto)
    }

    fun postImagem(imagem: ImagemDTO) : Call<ResponseBody> {
        return RetrofitClient.instance.postImagem(imagem)
    }
}