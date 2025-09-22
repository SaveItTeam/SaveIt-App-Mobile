package com.example.projetosaveit.model

class ImagemDTO {
    var id : Long = 0
    var image : String
    var product_id : Long

    constructor(image: String, product_id: Long) {
        this.image = image
        this.product_id = product_id
    }
}