package com.example.projetosaveit.api.repository

import com.example.projetosaveit.model.EmpresaDTO
import retrofit2.Call

class EmpresaRepository {
    fun getEmpresa(email: String): Call<EmpresaDTO> {
        return RetrofitClient.instance.getEmrpresaEmail(email)
    }
}