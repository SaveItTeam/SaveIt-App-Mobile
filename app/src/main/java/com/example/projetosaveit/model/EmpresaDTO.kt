package com.example.projetosaveit.model

class EmpresaDTO {
    var id : Long
    var cnpj: String
    var nome : String
    var codigo : String
    var email : String
    var tipo_usuario : String
    var telefone : String
    var endereco_id : Long
    var senha : String
    var categoria : String
    var is_buyer : Boolean

    constructor(
        is_buyer: Boolean,
        categoria: String,
        senha: String,
        endereco_id: Long,
        telefone: String,
        tipo_usuario: String,
        email: String,
        codigo: String,
        nome: String,
        cnpj: String,
        id: Long
    ) {
        this.is_buyer = is_buyer
        this.categoria = categoria
        this.senha = senha
        this.endereco_id = endereco_id
        this.telefone = telefone
        this.tipo_usuario = tipo_usuario
        this.email = email
        this.codigo = codigo
        this.nome = nome
        this.cnpj = cnpj
        this.id = id
    }
}