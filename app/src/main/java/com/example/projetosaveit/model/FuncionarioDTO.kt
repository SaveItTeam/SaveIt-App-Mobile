package com.example.projetosaveit.model

data class FuncionarioDTO (
    val id: Long,
    val name: String,
    val email: String,
    val enterpriseId: Long,
    val admin: Boolean
)