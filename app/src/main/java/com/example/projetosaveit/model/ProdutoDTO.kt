package com.example.projetosaveit.model

class ProdutoDTO {
    var id : Long
    var sku : String
    var nome : String
    var marca : String
    var descricao : String
    var empresa_id : Long

    constructor(
        empresa_id: Long,
        descricao: String,
        marca: String,
        nome: String,
        sku: String,
        id: Long
    ) {
        this.empresa_id = empresa_id
        this.descricao = descricao
        this.marca = marca
        this.nome = nome
        this.sku = sku
        this.id = id
    }
}