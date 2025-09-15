package com.example.projetosaveit.model

class ProdutoDTO {
    var id : Long = 0
    var name : String
    var brand : String
    var enterprise_id : Long

    constructor(name: String, brand: String, enterprise_id: Long) {
        this.name = name
        this.brand = brand
        this.enterprise_id = enterprise_id
    }
}