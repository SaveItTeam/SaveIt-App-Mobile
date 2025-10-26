package com.example.projetosaveit.api.network

import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.projetosaveit.BuildConfig

object RetrofitClientSql {

    private const val BASE_URL = "https://apisaveit.onrender.com"

    private val EMAIL = BuildConfig.API_EMAIL
    private val PASSWORD = BuildConfig.API_PASSWORD

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val original = chain.request()
            val newRequest = original.newBuilder()
                .addHeader("Authorization", Credentials.basic(EMAIL, PASSWORD))
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
