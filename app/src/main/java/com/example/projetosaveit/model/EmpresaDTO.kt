package com.example.projetosaveit.model

class EmpresaDTO(
    var id : Long,
    var cnpj: String,
    var name : String,
    var email : String,
    var plan_id : String,
    var phone_number : String,
    var address_id : Long,
    var password : String,
    var category : String,
    var is_buyer : Boolean
) {
}