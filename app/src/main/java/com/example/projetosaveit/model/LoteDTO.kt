package com.example.projetosaveit.model

import java.util.Date

class LoteDTO {
    val id: Long = 0
    val unite_measure: String?
    val entry_date: Date?
    val batch_code: String?
    val expiriation_date: Date?
    val quantity : Int
    val product_id: Long

    constructor(
        unite_measure: String?,
        entry_date: Date?,
        batch_code: String?,
        expiriation_date: Date?,
        quantity: Int,
        product_id: Long
    ) {
        this.unite_measure = unite_measure
        this.entry_date = entry_date
        this.batch_code = batch_code
        this.expiriation_date = expiriation_date
        this.quantity = quantity
        this.product_id = product_id
    }
}