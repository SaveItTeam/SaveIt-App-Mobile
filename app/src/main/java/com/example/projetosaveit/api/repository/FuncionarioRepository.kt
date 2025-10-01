package com.example.projetosaveit.api.repository

import com.example.projetosaveit.model.FuncionarioInsertDTO
import okhttp3.ResponseBody
import retrofit2.Call

class FuncionarioRepository {
    fun postFuncionario(funcionario: FuncionarioInsertDTO): Call<ResponseBody> {
        return RetrofitClient.instance.postFuncionario(funcionario)
    }
}