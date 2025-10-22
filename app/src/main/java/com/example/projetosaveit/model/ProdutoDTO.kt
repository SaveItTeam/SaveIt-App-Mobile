package com.example.projetosaveit.model

import com.google.gson.annotations.SerializedName

data class ProdutoDTO(
    var id : Long = 0,
    var name : String,
    var brand : String,
    var category : String,
    var description : String,
    var enterpriseId : Long
)