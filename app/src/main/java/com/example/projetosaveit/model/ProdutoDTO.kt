package com.example.projetosaveit.model

import com.google.gson.annotations.SerializedName

data class ProdutoDTO(
    var id : Long = 0,
    @SerializedName("name") var name : String,
    @SerializedName("brand") var brand : String,
    var enterprise_id : Long
)