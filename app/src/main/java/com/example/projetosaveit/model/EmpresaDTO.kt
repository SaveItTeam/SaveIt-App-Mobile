package com.example.projetosaveit.model

data class EmpresaDTO(
    var id : Long,
    var cnpj: String,
    var name : String,
    var email : String,
    var planId : Int,
    var phoneNumber : String,
    var addressId : Long,
    var password : String,
    var enterpriseImage : String
)