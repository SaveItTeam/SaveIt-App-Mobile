package com.example.projetosaveit.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class LoteDTO(
    val id: Long = 0,
    @SerializedName("unitMeasure") val unitMeasure: String,
    @SerializedName("entryDate") val entryDate: String,
    @SerializedName("batchCode")val batchCode: String,
    @SerializedName("expirationDate") val expirationDate: String,
    val maxQuantity : Int,
    val quantity : Int,
    @SerializedName("productId") val productId: Long
)