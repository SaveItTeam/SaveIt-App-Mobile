package com.example.projetosaveit.model

class LoteInsertDTO {
    var loteDTO : LoteDTO
    var imagemDTO : ImagemDTO
    var produtoDTO : ProdutoDTO

    constructor(produtoDTO: ProdutoDTO, imagemDTO: ImagemDTO, loteDTO: LoteDTO) {
        this.produtoDTO = produtoDTO
        this.imagemDTO = imagemDTO
        this.loteDTO = loteDTO
    }
}