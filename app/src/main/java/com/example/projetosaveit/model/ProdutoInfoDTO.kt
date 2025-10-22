package com.example.projetosaveit.model

import com.google.android.gms.common.api.Batch

class ProdutoInfoDTO(
    var productResponseDTO : ProdutoDTO,
    var batchResponseDTO : LoteDTO,
    var imageResponseDTO : ImagemDTO
) {
}