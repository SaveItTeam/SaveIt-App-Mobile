package com.example.projetosaveit.model

import android.R

class EnderecoDTO(
    var id : Long,
    var state : String,
    var city : String,
    var public_place : String,
    var cep : String,
    var neighbourhood : String,
    var complement : String,
    var house_number : Int
) {}