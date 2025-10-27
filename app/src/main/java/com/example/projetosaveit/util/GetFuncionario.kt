package com.example.projetosaveit.util

import android.util.Log
import com.example.projetosaveit.api.repository.FuncionarioRepository
import com.example.projetosaveit.model.FuncionarioDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object GetFuncionario {

    private val repositoryFunc = FuncionarioRepository()

    fun pegarEmailFunc(email: String, onResult: (FuncionarioDTO?) -> Unit) {
        Log.d("erro", "pegarEmailFuncionario: buscando funcionário para email = $email")

        repositoryFunc.getFuncionarioPorEmail(email).enqueue(object : Callback<FuncionarioDTO> {
            override fun onResponse(call: Call<FuncionarioDTO>, response: Response<FuncionarioDTO>) {
                Log.d("teste", "getFuncionario onResponse code=${response.code()}")
                if (response.isSuccessful) {
                    Log.d("teste", "getFuncionario body=${response.body()}")
                    onResult(response.body())
                } else {
                    val err = try { response.errorBody()?.string() } catch (e: Exception) { "erro lendo body" }
                    Log.e("erro", "getFuncionario erro do servidor: code=${response.code()} body=$err")
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<FuncionarioDTO>, t: Throwable) {
                Log.e("erro", "getFuncionario onFailure", t)
                onResult(null)
            }
        })
    }
}
