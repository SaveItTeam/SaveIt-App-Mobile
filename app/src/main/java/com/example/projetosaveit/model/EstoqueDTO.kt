package com.example.projetosaveit.model

class EstoqueDTO {
    var id : Long
    var quantidade : Int
    var quantidade_estoque : Int

    constructor(quantidade_estoque: Int, quantidade: Int, id: Long) {
        this.quantidade_estoque = quantidade_estoque
        this.quantidade = quantidade
        this.id = id
    }

}