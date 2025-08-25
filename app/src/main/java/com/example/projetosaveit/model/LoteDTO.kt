package com.example.projetosaveit.model

import java.util.Date

class LoteDTO {
    var id : Long
    var unite_measure : String
    var entry_date : Date
    var batch_code : String
    var expiration_date : Date
    var quantity : Int
    var product_id : Int

    constructor(
        product_id: Int,
        quantity: Int,
        expiration_date: Date,
        batch_code: String,
        entry_date: Date,
        unite_measure: String,
        id: Long
    ) {
        this.product_id = product_id
        this.quantity = quantity
        this.expiration_date = expiration_date
        this.batch_code = batch_code
        this.entry_date = entry_date
        this.unite_measure = unite_measure
        this.id = id
    }


}