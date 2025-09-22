package com.example.projetosaveit.adapter.recycleView

import java.util.Date

class Produto {
    var nomeProduto : String
    var quantidade : Int
    var validade : Date
    var imagem : String
    var idProduto : Long


    constructor(
        idProduto: Long,
        imagem: String,
        validade: Date,
        quantidade: Int,
        nomeProduto: String
    ) {
        this.idProduto = idProduto
        this.imagem = imagem
        this.validade = validade
        this.quantidade = quantidade
        this.nomeProduto = nomeProduto
    }

}