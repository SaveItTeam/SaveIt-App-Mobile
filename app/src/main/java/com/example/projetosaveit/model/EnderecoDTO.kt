package com.example.projetosaveit.model

data class EnderecoDTO(
    var id : Long,
    var state : String,
    var city : String,
    var publicPlace : String,
    var cep : String,
    var neighbourhood : String,
    var complement : String,
    var houseNumber : Int
)