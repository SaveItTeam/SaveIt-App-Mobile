package com.example.projetosaveit.adapter.recycleView

import java.util.Date

data class Produto(
    var name : String,
    var quantity : Int,
    var expirationDate : Date,
    var unitMeasure : String,
    var image : String,
    var id : Long
)