package com.example.projetosaveit.model

class VitrineDTO(
    val id : Long,
    var name: String,
    var description: String,
    var image: String,
    var loteId: Long,
    var tipoPeso: String,
    var quantidadeGeral: Int,
    var empresa: String,
    var localizacao: String,
    var validade: String,
    var enterpriseId: Long
) {
}
