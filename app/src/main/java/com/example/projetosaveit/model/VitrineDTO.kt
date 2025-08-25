package com.example.projetosaveit.model

class VitrineDTO {
    var id : Long
    var descricao : String
    var preco : Double
    var lote_id : Long

    constructor(lote_id: Long, preco: Double, descricao: String, id: Long) {
        this.lote_id = lote_id
        this.preco = preco
        this.descricao = descricao
        this.id = id
    }
}