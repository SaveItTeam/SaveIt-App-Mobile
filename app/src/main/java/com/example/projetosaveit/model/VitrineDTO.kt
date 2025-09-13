package com.example.projetosaveit.model

class VitrineDTO {
    var id : Long
    var descricao : String
    var preco : Double
    var lote_id : Long
    var nome : String
    var imagem : String

    constructor(lote_id: Long, preco: Double, descricao: String, id: Long, nome: String, imagem: String) {
        this.lote_id = lote_id
        this.preco = preco
        this.descricao = descricao
        this.id = id
        this.nome = nome
        this.imagem = imagem
    }
}