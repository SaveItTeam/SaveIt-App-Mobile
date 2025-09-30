package com.example.projetosaveit.model

class EmpresaDTO(
    var id : Long,
    var cnpj: String,
    var name : String,
    var email : String,
    var plan_id : Int,
    var phone_number : String,
    var address_id : Long,
    var password : String
) {
}