package com.example.projetosaveit.model

data class FuncionarioInsertDTO(
    val name: String,
    val email: String,
    val password: String,
    val enterprise_id: Long,
    val is_admin: Boolean
)
