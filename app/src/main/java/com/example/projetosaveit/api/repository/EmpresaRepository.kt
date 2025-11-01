package com.example.projetosaveit.api.repository

import com.example.projetosaveit.api.network.RetrofitClientSql
import com.example.projetosaveit.model.EmpresaDTO
import com.example.projetosaveit.model.EmpresaInsertDTO
import okhttp3.ResponseBody
import retrofit2.Call

class EmpresaRepository {
    fun getEmpresa(email: String): Call<EmpresaDTO> {
        return RetrofitClientSql.instance.getEmpresaEmail(email)
    }

    fun getEmpresaId(id: Long): Call<EmpresaDTO> {
        return RetrofitClientSql.instance.getEmpresaId(id)
    }

    fun postEmpresa(empresa : EmpresaInsertDTO) : Call<ResponseBody> {
        return RetrofitClientSql.instance.postEmpresa(empresa)
    }

    fun patchEmpresa(id : Long, updates : Map<String, @JvmSuppressWildcards Any>) : Call<ResponseBody> {
        return RetrofitClientSql.instance.patchEmpresaId(id, updates)
    }

    fun deleteEmpresa(enterpriseId : Long) : Call<ResponseBody> {
        return RetrofitClientSql.instance.deleteEmpresa(enterpriseId)
    }
}