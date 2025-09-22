package com.example.projetosaveit.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class LoteDTO(
    val id: Long = 0,
    @SerializedName("unit_measure") val unit_measure: String,
    @SerializedName("entry_date") val entry_date: String,
    @SerializedName("batch_code")val batch_code: String,
    @SerializedName("expiration_date") val expiration_date: String,
    val quantity : Int,
    @SerializedName("product_id") val product_id: Long
) {
}