package com.example.projetosaveit.model

import java.time.LocalDateTime

class EstoqueInsertDTO(
    var quantityInput : Int,
    var quantityOutput : Int,
    var batchId : Long,
    var productId : Long,
    var discardQuantity : Int,
    var discardReason : String,
    var createdAt : String
)
