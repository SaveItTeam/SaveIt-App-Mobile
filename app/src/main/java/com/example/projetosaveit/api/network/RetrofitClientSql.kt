package com.example.projetosaveit.api.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

object RetrofitClientSql {

    private const val BASE_URL = "https://apisaveit.onrender.com"
    private const val API_TOKEN = "essentiasaveit-193812-paoea-oei" // mesmo que está nas variáveis de ambiente do Render

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val newRequest = original.newBuilder()
                .addHeader("Authorization", "Bearer $API_TOKEN")
                .build()
            chain.proceed(newRequest)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val instance: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
