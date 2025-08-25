package com.example.projetosaveit.api.repository


class ProdutoRepository {
    fun getProdutos() = RetrofitClient.instance.getProdutos()
}