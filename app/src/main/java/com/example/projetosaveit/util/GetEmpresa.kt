package com.example.projetosaveit.util

import android.util.Log
import com.example.projetosaveit.api.repository.EmpresaRepository
import com.example.projetosaveit.model.EmpresaDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object GetEmpresa {
    val repositoryEmp : EmpresaRepository = EmpresaRepository()
    fun pegarEmailEmpresa(email: String, onResult: (EmpresaDTO?) -> Unit) {
        Log.d("erro", "pegarEmailEmpresa: buscando empresa para email = $email")
        repositoryEmp.getEmpresa(email).enqueue(object : Callback<EmpresaDTO> {
            override fun onResponse(call: Call<EmpresaDTO>, response: Response<EmpresaDTO>) {
                Log.d("teste", "getEmpresa onResponse code=${response.code()}")
                if (response.isSuccessful) {
                    Log.d("teste", "getEmpresa body=${response.body()}")
                    onResult(response.body())
                } else {
                    val err = try { response.errorBody()?.string() } catch (e: Exception) { "erro lendo body" }
                    Log.e("erro", "getEmpresa erro do servidor: code=${response.code()} body=$err")
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<EmpresaDTO>, t: Throwable) {
                Log.e("erro", "getEmpresa onFailure", t)
                onResult(null)
            }
        })
    }
}
