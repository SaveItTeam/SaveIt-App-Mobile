package com.example.projetosaveit.model

import okhttp3.Address

data class EmpresaInsertDTO(
    val enterprise : EmpresaDTO,
    val address: EnderecoDTO
)