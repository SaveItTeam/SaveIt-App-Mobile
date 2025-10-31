package com.example.projetosaveit.adapter.recycleView

import java.util.Date

data class Produto(
    var name : String,
    var quantity : String,
    var expirationDate : Date,
    var image : String,
    var batchId : Long,
    var id : Long
)