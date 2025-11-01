package com.example.projetosaveit.api.network

import com.example.projetosaveit.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

object RetrofitClientSql {

    const val apiToken = BuildConfig.API_TOKEN

    private const val BASE_URL = "https://apisaveit.onrender.com"
    private val API_TOKEN = apiToken ?: ""

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
