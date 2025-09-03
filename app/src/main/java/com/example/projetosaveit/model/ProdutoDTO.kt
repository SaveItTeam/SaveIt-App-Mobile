package com.example.projetosaveit.model

class ProdutoDTO {
    var id : Long
    var sku : String
    var name : String
    var brand : String
    var descricao : String
    var enterprise_id : Long

    constructor(
        enterprise_id: Long,
        descricao: String,
        brand: String,
        name: String,
        sku: String,
        id: Long
    ) {
        this.enterprise_id = enterprise_id
        this.descricao = descricao
        this.brand = brand
        this.name = name
        this.sku = sku
        this.id = id
    }
}