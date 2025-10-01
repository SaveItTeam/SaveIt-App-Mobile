package com.example.projetosaveit.model

data class VitrineDTO (
    var name: String,
    var description: String,
    var image: String,
    var price: Double,
    var productId: Long,
    var loteId: Long,
    var tipoPeso: String,
    var quantidadeGeral: Int,
    var empresa: String,
    var localizacao: String,
    var validade: String
)
