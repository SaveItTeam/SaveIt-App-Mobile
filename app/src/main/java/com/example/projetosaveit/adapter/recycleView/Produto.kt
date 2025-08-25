package com.example.projetosaveit.adapter.recycleView

import java.util.Date

class Produto {
    var nomeProduto : String
    var validade : Date
    var quantidade : Int

    constructor(quantidade: Int, validade: Date, nomeProduto: String
    ) {
        this.quantidade = quantidade
        this.validade = validade
        this.nomeProduto = nomeProduto
    }
}