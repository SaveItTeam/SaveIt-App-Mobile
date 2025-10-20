package com.example.projetosaveit.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class LoteDTO(
    val id: Long = 0,
    @SerializedName("unit_measure") val unitMeasure: String,
    @SerializedName("entry_date") val entryDate: String,
    @SerializedName("batch_code") val batchCode: String,
    @SerializedName("expiration_date") val expirationDate: String,
    val quantityMeasure : Int,
    @SerializedName("product_id") val productId: Long
)