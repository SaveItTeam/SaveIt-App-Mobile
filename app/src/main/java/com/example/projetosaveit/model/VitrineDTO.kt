package com.example.projetosaveit.model

class VitrineDTO {
    var name: String
    var description: String
    var image: String
    var price: Double
    var productId: Long
    var loteId: Long
    var tipoPeso: String
    var quantidadeGeral: Int
    var empresa: String
    var localizacao: String
    var validade: String

    constructor(
        name: String,
        description: String,
        image: String,
        price: Double,
        productId: Long,
        loteId: Long,
        tipoPeso: String,
        quantidadeGeral: Int,
        empresa: String,
        localizacao: String,
        validade: String
    ) {
        this.name = name
        this.description = description
        this.image = image
        this.price = price
        this.productId = productId
        this.loteId = loteId
        this.tipoPeso = tipoPeso
        this.quantidadeGeral = quantidadeGeral
        this.empresa = empresa
        this.localizacao = localizacao
        this.validade = validade
    }
}
