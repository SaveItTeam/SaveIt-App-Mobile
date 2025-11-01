package com.example.projetosaveit.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.projetosaveit.api.repository.EmpresaRepository
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner

class AppLifecycleObserver(private val empresaId: Int) : DefaultLifecycleObserver {

    private val repository = EmpresaRepository()

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        registrarEntrada()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        registrarSaida()
    }

    private fun registrarEntrada() {
        repository.registrarEntrada(empresaId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val body = response.body()?.string() ?: "vazio"
                    Log.d("AppLifecycle", "Entrada registrada: $body")
                    Log.d("AppLifecycle", "Resposta completa: ${response.errorBody()?.string()}")
                } else {
                    Log.e("AppLifecycle", "Erro na resposta de entrada: ${response.code()}")
                    Log.d("AppLifecycle", "Resposta completa: ${response.errorBody()?.string()}")

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("AppLifecycle", "Erro ao registrar entrada", t)
            }
        })
    }

    private fun registrarSaida() {
        repository.registrarSaida(empresaId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val body = response.body()?.string() ?: "vazio"
                    Log.d("AppLifecycle", "Saída registrada: $body")
                    Log.d("AppLifecycle", "Resposta completa: ${response.errorBody()?.string()}")
                } else {
                    Log.e("AppLifecycle", "Erro na resposta de saída: ${response.code()}")
                    Log.d("AppLifecycle", "Resposta completa: ${response.errorBody()?.string()}")

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("AppLifecycle", "Erro ao registrar saída", t)

            }
        })
    }

    companion object {
        fun iniciar(empresaId: Int) {
            val observer = AppLifecycleObserver(empresaId)
            ProcessLifecycleOwner.get().lifecycle.addObserver(observer)
        }
    }
}