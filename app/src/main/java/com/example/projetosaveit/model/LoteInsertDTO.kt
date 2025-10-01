package com.example.projetosaveit.model

data class LoteInsertDTO(
    var batch : LoteDTO,
    var product : ProdutoDTO,
    var image : ImagemDTO
)