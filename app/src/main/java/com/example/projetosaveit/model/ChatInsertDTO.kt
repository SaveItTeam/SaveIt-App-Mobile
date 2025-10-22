package com.example.projetosaveit.model

import com.google.gson.annotations.SerializedName

data class ChatInsertDTO(
    @SerializedName("text") val text: String,
    @SerializedName("sentAt") val sentAt: String,
    @SerializedName("enterpriseId") val enterpriseId: Long,
    @SerializedName("otherEnterpriseId") val otherEnterpriseId: Long,
    @SerializedName("read") val read: Boolean = false
)