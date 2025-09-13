package com.example.projetosaveit.api.repository

class VitrineRepository {
    fun getVitrine() = RetrofitClient.instance.getVitrine()
}