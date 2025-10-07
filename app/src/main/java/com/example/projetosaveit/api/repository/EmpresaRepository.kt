package com.example.projetosaveit.api.repository

import com.example.projetosaveit.model.EmpresaDTO
import com.example.projetosaveit.model.EmpresaInsertDTO
import okhttp3.ResponseBody
import retrofit2.Call

class EmpresaRepository {
    fun getEmpresa(email: String): Call<EmpresaDTO> {
        return RetrofitClient.instance.getEmpresaEmail(email)
    }

    fun postEmpresa(empresa : EmpresaInsertDTO) : Call<ResponseBody> {
        return RetrofitClient.instance.postEmpresa(empresa)
    }

    fun patchEmpresa(id : Long, updates : Map<String, @JvmSuppressWildcards Any>) : Call<ResponseBody> {
        return RetrofitClient.instance.patchEmpresaId(id, updates)
    }
}