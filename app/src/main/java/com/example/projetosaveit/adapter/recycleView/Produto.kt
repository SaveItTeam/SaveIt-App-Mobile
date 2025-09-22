package com.example.projetosaveit.adapter.recycleView

import java.util.Date

class Produto(
    var name : String,
    var quantity : Int,
    var expiration_date : Date,
    var image : String,
    var id : Long
) {}