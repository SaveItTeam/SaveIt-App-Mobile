package com.example.projetosaveit.model

import com.google.gson.annotations.SerializedName

class ImagemDTO(
    var id : Long = 0,
    @SerializedName("image") var image : String,
    var product_id : Long
)