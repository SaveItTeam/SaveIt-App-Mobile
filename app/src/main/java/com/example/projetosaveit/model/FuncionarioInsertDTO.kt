package com.example.projetosaveit.model

data class FuncionarioInsertDTO(
    val name: String,
    val email: String,
    val password: String,
    val enterpriseId: Long,
    val isAdmin: Boolean
)
